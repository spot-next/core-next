package io.spotnext.core.infrastructure.scheduling.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.scheduling.support.AbstractCronJobPerformable;
import io.spotnext.core.infrastructure.scheduling.support.AbstractCronJobPerformable.PerformResult;
import io.spotnext.core.infrastructure.scheduling.support.CronJobException;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.core.infrastructure.support.init.ModuleInit;
import io.spotnext.core.management.exception.RemoteServiceInitException;
import io.spotnext.core.persistence.query.JpqlQuery;
import io.spotnext.core.persistence.query.LambdaQuery;
import io.spotnext.core.persistence.query.QueryResult;
import io.spotnext.core.persistence.service.QueryService;
import io.spotnext.itemtype.core.beans.CronJobData;
import io.spotnext.itemtype.core.enumeration.CronJobStatus;
import io.spotnext.itemtype.core.scheduling.AbstractCronJob;
import io.spotnext.support.util.ValidationUtil;

@Service
@DependsOn("persistenceService")
public class DefaultCronJobService extends AbstractService implements CronJobService {

	@Value("${service.cronjob.scheduleonboot:}")
	private boolean scheduleCronJobsOnBoot = true;

	@Value("${service.cronjob.poolsize:}")
	private int poolsize = 5;

	private final Map<String, AbstractCronJobPerformable<AbstractCronJob>> registeredPerformables = new HashMap<>();
	private final Map<AbstractCronJob, PerformableHolder> runningCronJobs = new HashMap<>();
	private final Map<AbstractCronJob, ScheduledFuture<?>> scheduledCronJobs = new HashMap<>();

	private ThreadPoolTaskScheduler taskScheduler;

	@Autowired
	private QueryService queryService;

	@EventListener(classes = ApplicationReadyEvent.class)
	public void onApplicationReady(final ApplicationReadyEvent event) throws RemoteServiceInitException {
		if (ModuleInit.isBootComplete(event.getApplicationContext())) {
			taskScheduler = new ThreadPoolTaskScheduler();
			taskScheduler.setPoolSize(poolsize);
			taskScheduler.setThreadNamePrefix("CronJob");
			taskScheduler.initialize();
			taskScheduler.setRemoveOnCancelPolicy(true);

			if (scheduleCronJobsOnBoot) {
				scheduleCronjobs();
			}
		}
	}

	@Override
	public void registerPerformable(String beanName, AbstractCronJobPerformable<AbstractCronJob> performable) {
		Logger.debug(String.format("Registering performable '%s' of type %s", beanName, performable.getClass().getName()));
		registeredPerformables.put(beanName, performable);
	}

	protected void scheduleCronjobs() {
		final LambdaQuery<AbstractCronJob> jobsQuery = new LambdaQuery<>(AbstractCronJob.class);
		final QueryResult<AbstractCronJob> result = queryService.query(jobsQuery);

		for (var job : result.getResults()) {
			try {
				startCronJob(job, false);
			} catch (CronJobException e) {
				Logger.error(String.format("Could not start cronjob '%s'", job.getUid()));
			}
		}
	}

	@Override
	public void startCronJob(String cronJobUid) throws CronJobException {
		final var cronJob = getCronJobByUid(cronJobUid);

		ValidationUtil.validateNotNull("No cronjob with the given UID found!", cronJob);

		startCronJob(cronJob);
	}

	@Override
	public void startCronJob(AbstractCronJob cronJob) throws CronJobException {
		startCronJob(cronJob, true);
	}

	protected void startCronJob(AbstractCronJob cronJob, boolean startImmediately) throws CronJobException {
		final var cronTabEntry = getCronTabEntry(cronJob);

		if (cronTabEntry.isPresent()) {
			final var cronTrigger = new CronTrigger(cronTabEntry.get());
			final var performable = registeredPerformables.get(cronJob.getPerformable());

			if (performable != null) {
				modelService.refresh(cronJob);

				// only allow to start the cronjob if is is not already running
				if (!CronJobStatus.RUNNING.equals(cronJob.getStatus())) {
					if (startImmediately) {
						taskScheduler.schedule(() -> performCronjob(cronJob, performable), new Date());

					} else if (!scheduledCronJobs.containsKey(cronJob)) { // only scheduled if not already scheduled!
						final var scheduled = taskScheduler.schedule(() -> performCronjob(cronJob, performable), cronTrigger);
						scheduledCronJobs.put(cronJob, scheduled);
					}
				} else {
					throw new CronJobException(String.format("Cronjob '%s' could not be started, as it is already running.", cronJob.getUid()));
				}
			} else {
				Logger.warn(String.format("CronJob '%s' has defined an unknown performable '%s'.", cronJob.getUid(), cronJob.getPerformable()));
			}
		} else {
			Logger.debug(String.format("CronJob '%s' has no trigger set.", cronJob.getUid()));
		}
	}

	@Override
	public List<CronJobData> getAllCronJobs(CronJobStatus... states) {
		var query = new JpqlQuery<>(String.format("SELECT c FROM %s AS c WHERE c.status IN :status", AbstractCronJob.class.getSimpleName()),
				AbstractCronJob.class);

		final List<CronJobStatus> allowedStates;

		if (ArrayUtils.isNotEmpty(states)) {
			allowedStates = Arrays.asList(states);
		} else {
			allowedStates = new ArrayList<>();
			allowedStates.addAll(Arrays.asList(CronJobStatus.values()));
		}

		query.addParam("status", allowedStates);

		var result = queryService.query(query);

		return result.getResults().stream().map(this::convertCronJob).collect(Collectors.toList());
	}

	/**
	 * The thread that the cronjob performable is running on will be interrupted which signals the performable to abort. This will only work though, if the
	 * performable regularly calls {@link AbstractCronJobPerformable#abortIfRequested()}!
	 */
	@Override
	public boolean abortCronJob(String cronJobUid) throws IllegalArgumentException {
		final var cronJob = getCronJobByUid(cronJobUid);

		ValidationUtil.validateNotNull("No cronjob with the given UID found!", cronJob);

		var runningPerformable = runningCronJobs.get(cronJob);

		if (runningPerformable != null) {
			runningPerformable.getThread().interrupt();
			return true;
		}

		return false;
	}

	protected AbstractCronJob getCronJobByUid(String cronJobUid) {
		return modelService.get(AbstractCronJob.class, Collections.singletonMap(AbstractCronJob.PROPERTY_UID, cronJobUid));
	}

	protected CronJobData convertCronJob(AbstractCronJob item) {
		var data = new CronJobData();

		data.setUid(item.getUid());
		data.setLastStarted(item.getLastStarted());
		data.setLastFinished(item.getLastFinished());
		data.setRunning(runningCronJobs.containsKey(item));
		data.setLastResult(item.getResult());
		data.setNumberOfRetries(item.getNumberOfRetries());

		return data;
	}

	protected void performCronjob(AbstractCronJob cronJob, AbstractCronJobPerformable<AbstractCronJob> performable) {
		// assume it will fail ;-)
		PerformResult result = AbstractCronJobPerformable.FAILURE;

		try {
			// register cronjob as running
			runningCronJobs.put(cronJob, new PerformableHolder(Thread.currentThread(), performable));

			// refresh the items (this is another thread!) and update the start time
			modelService.refresh(cronJob);
			cronJob.setStatus(CronJobStatus.RUNNING);
			cronJob.setLastStarted(LocalDateTime.now());
			modelService.save(cronJob);

			if (cronJob.getNumberOfRetries() < cronJob.getMaxRetriesOnFailure()) {
				result = performable.start(cronJob);

				// reset the number of retries, as it has been performed successfully
				modelService.refresh(cronJob);
				cronJob.setNumberOfRetries(0);
				modelService.save(cronJob);
			} else {
				Logger.warn(String.format("Cronjob '%s' failed %s times out of %s and will be unscheduled.", cronJob.getUid(), cronJob.getNumberOfRetries(),
						cronJob.getMaxRetriesOnFailure()));

				// unschedule the cronjob as it has failed to often!
				scheduledCronJobs.get(cronJob).cancel(true);
			}
		} catch (Throwable e) {
			increaseRetryCounter(cronJob);

			Logger.error(String.format("Cronjob '%s' failed - trying %s more times", cronJob.getUid(),
					cronJob.getMaxRetriesOnFailure() - cronJob.getNumberOfRetries()));
		} finally {
			// remove running marker, so that it can be restarted
			runningCronJobs.remove(cronJob);

			modelService.refresh(cronJob);
			cronJob.setResult(result.getResult());
			cronJob.setStatus(result.getStatus());
			cronJob.setLastFinished(LocalDateTime.now());
			modelService.save(cronJob);
		}
	}

	private void increaseRetryCounter(AbstractCronJob cronJob) {
		cronJob.setNumberOfRetries(cronJob.getNumberOfRetries() + 1);
	}

	private Optional<String> getCronTabEntry(AbstractCronJob cronjob) {
		if (cronjob.getTrigger() != null) {
			final var trigger = cronjob.getTrigger();
			return Optional.of(String.format("%s %s %s %s %s %s",
					StringUtils.defaultIfBlank(trigger.getSecond(), "0"),
					StringUtils.defaultIfBlank(trigger.getMinute(), "0"),
					StringUtils.defaultIfBlank(trigger.getHour(), "0"),
					StringUtils.defaultIfBlank(trigger.getDayOfMonth(), "*"),
					StringUtils.defaultIfBlank(trigger.getMonth(), "*"),
					StringUtils.defaultIfBlank(trigger.getWeekDay(), "*")));
		}

		return Optional.empty();
	}

	protected static class PerformableHolder {
		private final Thread thread;
		private final AbstractCronJobPerformable<AbstractCronJob> performable;

		public PerformableHolder(Thread thread, AbstractCronJobPerformable<AbstractCronJob> performable) {
			this.thread = thread;
			this.performable = performable;
		}

		public Thread getThread() {
			return thread;
		}

		public AbstractCronJobPerformable<AbstractCronJob> getPerformable() {
			return performable;
		}

	}
}

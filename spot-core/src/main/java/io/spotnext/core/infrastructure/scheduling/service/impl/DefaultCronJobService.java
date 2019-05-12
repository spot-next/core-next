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
import io.spotnext.itemtype.core.enumeration.CronJobResult;
import io.spotnext.itemtype.core.enumeration.CronJobStatus;
import io.spotnext.itemtype.core.scheduling.AbstractCronJob;
import io.spotnext.support.util.ValidationUtil;

@Service
@DependsOn("persistenceService")
public class DefaultCronJobService extends AbstractService implements CronJobService {

	@Value("${service.cronjob.startonboot:}")
	private boolean startCronJobsOnBoot = true;

	@Value("${service.cronjob.poolsize:}")
	private int poolsize = 5;

	private ThreadPoolTaskScheduler taskScheduler;

	private Map<AbstractCronJob, ScheduledFuture<?>> runningCronJobs = new HashMap<>();
	private Map<String, AbstractCronJobPerformable<AbstractCronJob>> performables = new HashMap<>();

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

			if (startCronJobsOnBoot) {
				startCronjobs();
			}
		}
	}

	@Override
	public void registerPerformable(String beanName, AbstractCronJobPerformable<AbstractCronJob> performable) {
		Logger.debug(String.format("Registering performable '%s' of type %s", beanName, performable.getClass().getName()));
		performables.put(beanName, performable);
	}

	protected void startCronjobs() {
		final LambdaQuery<AbstractCronJob> jobsQuery = new LambdaQuery<>(AbstractCronJob.class);
		final QueryResult<AbstractCronJob> result = queryService.query(jobsQuery);

		for (var job : result.getResults()) {
			try {
				startCronJob(job);
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
		startCronJob(cronJob, false);
	}

	protected void startCronJob(AbstractCronJob cronJob, boolean startImmediately) throws CronJobException {
		final var cronTabEntry = getCronTabEntry(cronJob);

		if (cronTabEntry.isPresent()) {
			final var cronTrigger = new CronTrigger(cronTabEntry.get());
			final AbstractCronJobPerformable<AbstractCronJob> performable = performables.get(cronJob.getPerformable());

			if (performable != null) {
				ScheduledFuture<?> runningCronJob = runningCronJobs.get(cronJob);

				if (runningCronJob == null || runningCronJob.isCancelled() || runningCronJob.isDone()) {
					final ScheduledFuture<?> schedule;

					// start the scheduled task either immediately or schedule it
					if (startImmediately) {
						schedule = taskScheduler.schedule(() -> performCronjob(cronJob, performable), new Date());
					} else {
						schedule = taskScheduler.schedule(() -> performCronjob(cronJob, performable), cronTrigger);
					}

					runningCronJobs.put(cronJob, schedule);
				} else {
					throw new CronJobException(String.format("Cronjob '%s' could not be started, as it is already scheduled.", cronJob.getUid()));
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

	@Override
	public boolean abortCronJob(String cronJobUid) throws IllegalArgumentException {
		final var cronJob = getCronJobByUid(cronJobUid);

		ValidationUtil.validateNotNull("No cronjob with the given UID found!", cronJob);

		ScheduledFuture<?> runningCronJob = runningCronJobs.get(cronJob);

		if (runningCronJob != null) {
			var cancel = runningCronJob.cancel(true);
			cronJob.setResult(CronJobResult.UNKNOWN);
			cronJob.setStatus(CronJobStatus.FINISHED);
			modelService.save(cronJob);

			if (cancel) {
				runningCronJobs.remove(cronJob);
				return cancel;
			}
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
		data.setStatus(item.getStatus());
		data.setResult(item.getResult());
		data.setNumberOfRetries(item.getNumberOfRetries());

		return data;
	}

	protected void performCronjob(AbstractCronJob cronJob, AbstractCronJobPerformable<AbstractCronJob> performable) {
		PerformResult result = null;

		// refresh the items (this is another thread!) and update the start time
		modelService.refresh(cronJob);
		cronJob.setStatus(CronJobStatus.RUNNING);
		cronJob.setLastStarted(LocalDateTime.now());
		modelService.save(cronJob);

		try {
			if (cronJob.getNumberOfRetries() < cronJob.getMaxRetriesOnFailure()) {
				result = performable.start(cronJob);

				// reset the number of retries, as it has been performed successfully
				modelService.refresh(cronJob);
				cronJob.setNumberOfRetries(0);
				modelService.save(cronJob);
			} else {
				Logger.warn(String.format("Cronjob '%s' failed %s times out of %s and will be halted.", cronJob.getUid(), cronJob.getNumberOfRetries(),
						cronJob.getMaxRetriesOnFailure()));

				abortCronJob(cronJob.getUid());
			}
		} catch (Throwable e) {
			Logger.error("Cronjob '%s' failed - trying %s more times");

			result = new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
			increaseRetryCounter(cronJob);

			throw e;
		} finally {
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
}

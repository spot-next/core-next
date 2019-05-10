package io.spotnext.core.infrastructure.scheduling.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

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
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.core.infrastructure.support.init.ModuleInit;
import io.spotnext.core.management.exception.RemoteServiceInitException;
import io.spotnext.core.persistence.query.LambdaQuery;
import io.spotnext.core.persistence.query.QueryResult;
import io.spotnext.core.persistence.service.QueryService;
import io.spotnext.itemtype.core.enumeration.CronJobResult;
import io.spotnext.itemtype.core.enumeration.CronJobStatus;
import io.spotnext.itemtype.core.scheduling.AbstractCronJob;

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
			startCronJob(job);
		}
	}

	@Override
	public void startCronJob(AbstractCronJob cronJob) {
		final var cronTabEntry = getCronTabEntry(cronJob);

		if (cronTabEntry.isPresent()) {
			final var cronTrigger = new CronTrigger(cronTabEntry.get());
			final AbstractCronJobPerformable<AbstractCronJob> performable = performables.get(cronJob.getPerformable());

			if (performable != null) {
				if (runningCronJobs.get(cronJob) != null) {
					ScheduledFuture<?> schedule = taskScheduler.schedule(() -> performCronjob(cronJob, performable), cronTrigger);
					runningCronJobs.put(cronJob, schedule);
				} else {
					Logger.warn(String.format("Cronjob '%s' could not be started, as it is already scheduled.", cronJob.getUid()));
				}
			} else {
				Logger.warn(String.format("CronJob '%s' has defined an unknown performable '%s'.", cronJob.getUid(), cronJob.getPerformable()));
			}
		} else {
			Logger.debug(String.format("CronJob '%s' has no trigger set.", cronJob.getUid()));
		}
	}

	protected void performCronjob(AbstractCronJob cronJob, AbstractCronJobPerformable<AbstractCronJob> performable) {
		PerformResult result = null;

		// refresh the items (this is another thread!) and update the start time
		modelService.refresh(cronJob);
		cronJob.setLastStarted(LocalDateTime.now());
		modelService.save(cronJob);

		try {
			if (cronJob.getNumberOfRetries() < cronJob.getMaxRetriesOnFailure()) {
				result = performable.perform(cronJob);

				// reset the number of retries, as it has been performed successfully
				cronJob.setNumberOfRetries(0);
			} else {
				Logger.warn(String.format("Cronjob '%s' failed %s times out of %s and will be halted.", cronJob.getUid(), cronJob.getNumberOfRetries(),
						cronJob.getMaxRetriesOnFailure()));

				ScheduledFuture<?> schedule = runningCronJobs.get(cronJob);
				schedule.cancel(false);
			}
		} catch (Throwable e) {
			Logger.error("Cronjob '%s' failed - trying %s more times");

			result = new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
			increaseRetryCounter(cronJob);

			throw e;
		} finally {
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

package io.spotnext.core.infrastructure.scheduling.support;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.scheduling.service.impl.CronJobService;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.core.infrastructure.support.spring.PostConstructor;
import io.spotnext.itemtype.core.enumeration.CronJobResult;
import io.spotnext.itemtype.core.enumeration.CronJobStatus;
import io.spotnext.itemtype.core.scheduling.AbstractCronJob;

@SuppressFBWarnings(value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
public abstract class AbstractCronJobPerformable<T extends AbstractCronJob> implements BeanNameAware, PostConstructor {

	@Autowired
	protected CronJobService cronJobService;

	protected String beanName;

	@Override
	public void setup() {
		cronJobService.registerPerformable(beanName, (AbstractCronJobPerformable<AbstractCronJob>) this);
	}

	/**
	 * Checks if the cronjob has been requested to abort and throws an exception that cancels code execution. The exception is handled/swallowed and will not be
	 * logged as a real exception.
	 * 
	 * @throws CronJobAbortException
	 */
	public void abortIfRequested() throws CronJobAbortException {
		// calling this also clears the thread status! (don't call it twice)
		if (Thread.currentThread().isInterrupted()) {
			throw new CronJobAbortException("Cronjob aborted");
		}
	}

	public PerformResult start(T cronJob) throws CronJobException {
		try {
			Logger.info(String.format("Starting cronjob '%s'", cronJob.getUid()));
			return perform(cronJob);
		} catch (CronJobAbortException e) {
			// this is a signal that the cronjob was requested to abort, so not a real exception
			Logger.info(String.format("Cronjob '%s' has been aborted", cronJob.getUid()));
		} finally {
			Logger.info(String.format("Finished cronjob '%s'", cronJob.getUid()));
		}

		return ABORTED;
	}

	/**
	 * The actual business logic of the cronjob performable.
	 * 
	 * @param cronJob the associated cronjob item.
	 * @return the result of the cronjob run.
	 */
	protected abstract PerformResult perform(T cronJob) throws CronJobException;

	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public static class PerformResult {
		private final CronJobResult result;
		private final CronJobStatus status;

		public PerformResult(CronJobResult result, CronJobStatus status) {
			this.result = result;
			this.status = status;
		}

		public CronJobResult getResult() {
			return result;
		}

		public CronJobStatus getStatus() {
			return status;
		}
	}

	public static final PerformResult SUCCESS = new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	public static final PerformResult ERROR = new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
	public static final PerformResult FAILURE = new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
	public static final PerformResult ABORTED = new PerformResult(CronJobResult.UNKNOWN, CronJobStatus.FINISHED);
}

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
	 * Requests to abort the cronjob. The implementation can decide when or if at all it aborts.
	 * 
	 * @param force if true, the cronjob will be killed!
	 */
	public abstract void requestAbort(boolean force);

	public PerformResult start(T cronJob) {
		try {
			return perform(cronJob);
		} catch (Throwable e) {
			Logger.exception(String.format("Cronjob '%s' failed", cronJob.getUid()), e);
		}

		return ABORTED;
	}

	/**
	 * The actual business logic of the cronjob performable.
	 * 
	 * @param cronJob the associated cronjob item.
	 * @return the result of the cronjob run.
	 */
	protected abstract PerformResult perform(T cronJob);

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

	protected PerformResult SUCCESS = new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	protected PerformResult ERROR = new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
	protected PerformResult FAILURE = new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
	protected PerformResult ABORTED = new PerformResult(CronJobResult.UNKNOWN, CronJobStatus.FINISHED);
}

package io.spotnext.core.infrastructure.scheduling.service.impl;

import io.spotnext.core.infrastructure.scheduling.support.AbstractCronJobPerformable;
import io.spotnext.itemtype.core.scheduling.AbstractCronJob;

public interface CronJobService {

	void registerPerformable(String beanName, AbstractCronJobPerformable<AbstractCronJob> abstractCronJobPerformable);

	void startCronJob(AbstractCronJob cronJob);

}

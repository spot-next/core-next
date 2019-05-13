package io.spotnext.core.infrastructure.scheduling.service.impl;

import java.util.List;

import io.spotnext.core.infrastructure.scheduling.support.AbstractCronJobPerformable;
import io.spotnext.core.infrastructure.scheduling.support.CronJobException;
import io.spotnext.itemtype.core.beans.CronJobData;
import io.spotnext.itemtype.core.enumeration.CronJobStatus;
import io.spotnext.itemtype.core.scheduling.AbstractCronJob;

public interface CronJobService {

	/**
	 * Register a performable with the given bean name. This is automatically called from every cronjob performable that inherits from
	 * {@link AbstractCronJobPerformable}.
	 * 
	 * @param beanName
	 * @param abstractCronJobPerformable
	 */
	void registerPerformable(String beanName, AbstractCronJobPerformable<AbstractCronJob> abstractCronJobPerformable);

	/**
	 * Starts the cronjob with the given UID.
	 * 
	 * @param cronJobUid the UID of the cronjob
	 * @throws IllegalArgumentException in case there is no crojob found for the given UID
	 * @throws CronJobException         if the cronjob is already running
	 */
	void startCronJob(String cronJobUid) throws IllegalArgumentException, CronJobException;

	/**
	 * Starts the given cronjob immediately.
	 * 
	 * @param cronJob the non-null cronjob item instance.
	 * @throws CronJobException if the cronjob is already running
	 */
	void startCronJob(AbstractCronJob cronJob) throws CronJobException;

	/**
	 * Aborts the cronjob with the given UID.
	 * 
	 * @param cronJobUid the UID of the cronjob
	 * @throws IllegalArgumentException in case there is no crojob found for the given UID
	 */
	boolean abortCronJob(String cronJobUid) throws IllegalArgumentException;

	/**
	 * Returns all cronjob objects
	 * 
	 * @param states limits the result to the given states. If null or empty, all states are used.
	 * @return
	 */
	List<CronJobData> getAllCronJobs(CronJobStatus... states);

}

package at.spot.core.persistence.service;

import java.util.concurrent.Callable;

public interface TransactionService {
	/**
	 * Starts a transaction and runs the given runnable. After the work has been
	 * done, {@link #commit()} is automatically called. If there is an exception,
	 * {@link #rollback()} is automatically invoked.
	 * 
	 * @param body
	 */
	<R> R execute(Callable<R> body);

	/**
	 * Starts a transaction in the given thread context. After the work has been
	 * done, either {@link #rollback()} or {@link #commit()} have to be invoked.
	 * Otherwise data might not be persisted.
	 */
	void start();

	/**
	 * Persists the data changes after {@link #start()} has been invoked. Nothing
	 * happens, if there is no active transaction.
	 */
	void commit();

	/**
	 * Discards all data changes that in the current thread since the invocation of
	 * {@link #start()}. Nothing happens, if there is no active transaction.
	 */
	void rollback();
}

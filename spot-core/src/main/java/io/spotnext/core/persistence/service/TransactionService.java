package io.spotnext.core.persistence.service;

import java.util.concurrent.Callable;

import org.springframework.transaction.TransactionException;

/**
 * <p>TransactionService interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface TransactionService {
	/**
	 * Starts a transaction and runs the given runnable. After the work has been
	 * done, {@link #commit()} is automatically called. If there is an exception,
	 * {@link #rollback()} is automatically invoked.
	 *
	 * @param body a {@link java.util.concurrent.Callable} object.
	 * @param <R> a R object.
	 * @return a R object.
	 * @throws org.springframework.transaction.TransactionException if any.
	 */
	<R> R execute(Callable<R> body) throws TransactionException;

	/**
	 * Starts a transaction and runs the given runnable. After the work has been
	 * done, {@link #commit()} is automatically called. If there is an exception,
	 * {@link #rollback()} is automatically invoked.
	 *
	 * @param body a {@link java.lang.Runnable} object.
	 * @throws org.springframework.transaction.TransactionException if any.
	 */
	void executeWithoutResult(Runnable body) throws TransactionException;

	/**
	 * Starts a transaction in the given thread context. After the work has been
	 * done, either {@link #rollback()} or {@link #commit()} have to be invoked.
	 * Otherwise data might not be persisted.
	 *
	 * @throws org.springframework.transaction.TransactionException if any.
	 */
	void start() throws TransactionException;

	/**
	 * Persists the data changes after {@link #start()} has been invoked. Nothing
	 * happens, if there is no active transaction.
	 *
	 * @throws org.springframework.transaction.TransactionException if any.
	 */
	void commit() throws TransactionException;

	/**
	 * Discards all data changes that in the current thread since the invocation of
	 * {@link #start()}. Nothing happens, if there is no active transaction.
	 *
	 * @throws org.springframework.transaction.TransactionException if any.
	 */
	void rollback() throws TransactionException;

	/**
	 * <p>isTransactionActive.</p>
	 *
	 * @return true if there is currently a transaction active in the current
	 *         thread.
	 */
	boolean isTransactionActive();
}

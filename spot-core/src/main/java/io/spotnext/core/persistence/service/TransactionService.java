package io.spotnext.core.persistence.service;

import java.util.Optional;
import java.util.concurrent.Callable;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

/**
 * <p>
 * TransactionService interface.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface TransactionService {
	/**
	 * Starts a transaction in the current thread context. After the work has been done, either {@link #rollback(TransactionStatus)} or
	 * {@link #commit(TransactionStatus)} have to be invoked. Otherwise data might not be persisted.
	 *
	 * @return the transaction status object for the newly created (or already active) transaction.
	 * @throws org.springframework.transaction.TransactionException if any.
	 */
	TransactionStatus start() throws TransactionException;

	/**
	 * Starts a transaction and runs the given runnable. After the work has been done, {@link #commit(TransactionStatus)} is automatically called. If there is
	 * an exception, {@link #rollback(TransactionStatus)} is automatically invoked.
	 *
	 * @param body a {@link java.util.concurrent.Callable} object.
	 * @param      <R> a R object.
	 * @return a R object.
	 * @throws org.springframework.transaction.TransactionException if any.
	 */
	<R> R execute(Callable<R> body) throws TransactionException;

	/**
	 * Starts a transaction and runs the given runnable. After the work has been done, {@link #commit(TransactionStatus)} is automatically called. If there is
	 * an exception, {@link #rollback(TransactionStatus)} is automatically invoked.
	 *
	 * @param body a {@link java.lang.Runnable} object.
	 * @throws org.springframework.transaction.TransactionException if any.
	 */
	void executeWithoutResult(Runnable body) throws TransactionException;

	/**
	 * Persists the data changes after {@link #start()} has been invoked. Nothing happens, if there is no active transaction.
	 *
	 * @throws TransactionException see {@link TransactionStatus#isCompleted()}
	 */
	void commit(TransactionStatus status) throws TransactionException;

	/**
	 * Discards all data changes that in the current thread since the invocation of {@link #start()}. Nothing happens, if there is no active transaction.
	 *
	 * @throws TransactionException see {@link PlatformTransactionManager#rollback(TransactionStatus)}
	 */
	void rollback(TransactionStatus status) throws TransactionException;

	/**
	 * <p>
	 * isTransactionActive.
	 * </p>
	 *
	 * @return true if there is currently a transaction active in the current thread.
	 */
	boolean isTransactionActive();

	/**
	 * Creates a savepoint in the current running transaction.
	 *
	 * @return the savepoint object
	 * @throws TransactionException see {@link TransactionStatus#createSavepoint()}
	 */
	Object createSavePoint(TransactionStatus status) throws TransactionException;

	/**
	 * Executes a rollback to the given savepoint.
	 * 
	 * @param savePoint the savepoint of the current running transaction.
	 * @throws TransactionException see {@link TransactionStatus#rollbackToSavepoint(Object)}
	 */
	void rollbackToSavePoint(TransactionStatus status, Object savePoint) throws TransactionException;

	/**
	 * @return returns the currently active transaction or null.
	 */
	Optional<TransactionStatus> getCurrentTransaction();
}

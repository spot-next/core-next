package io.spotnext.core.persistence.service;

import java.util.Optional;
import java.util.concurrent.Callable;

import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

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
	 * Starts a transaction and runs the given runnable. After the work has been
	 * done, {@link #commit()} is automatically called. If there is an
	 * exception, {@link #rollback()} is automatically invoked.
	 *
	 * @param body
	 *            a {@link java.util.concurrent.Callable} object.
	 * @param <R>
	 *            a R object.
	 * @return a R object.
	 * @throws org.springframework.transaction.TransactionException
	 *             if any.
	 */
	<R> R execute(Callable<R> body) throws TransactionException;

	/**
	 * Starts a transaction and runs the given runnable. After the work has been
	 * done, {@link #commit()} is automatically called. If there is an
	 * exception, {@link #rollback()} is automatically invoked.
	 *
	 * @param body
	 *            a {@link java.lang.Runnable} object.
	 * @throws org.springframework.transaction.TransactionException
	 *             if any.
	 */
	void executeWithoutResult(Runnable body) throws TransactionException;

	/**
	 * Starts a transaction in the given thread context. After the work has been
	 * done, either {@link #rollback()} or {@link #commit()} have to be invoked.
	 * Otherwise data might not be persisted.
	 *
	 * @throws org.springframework.transaction.TransactionException
	 *             if any.
	 */
	TransactionStatus start() throws TransactionException;

	/**
	 * Persists the data changes after {@link #start()} has been invoked.
	 * Nothing happens, if there is no active transaction.
	 *
	 * @throws org.springframework.transaction.TransactionException
	 *             if any.
	 */
	void commit(TransactionStatus status) throws TransactionException;

	/**
	 * Discards all data changes that in the current thread since the invocation
	 * of {@link #start()}. Nothing happens, if there is no active transaction.
	 *
	 * @throws org.springframework.transaction.TransactionException
	 *             if any.
	 */
	void rollback(TransactionStatus status) throws TransactionException;

	/**
	 * CHecks whether there is an active spring transaction (eg. via the
	 * {@link Transactional} annotation) or a manually manager one, using the
	 * {@link #start()} method.
	 * 
	 * @return true if there is an active transaction in the current thread
	 *         context
	 */
	boolean isTransactionActive();

	/**
	 * Creates a new savepoint for the currently active transaction.
	 *
	 * @return a {@link java.lang.Object} object.
	 * @throws org.springframework.transaction.TransactionException
	 *             if any.
	 */
	Object createSavePoint() throws TransactionException;

	/**
	 * Creates a savepoint in the current running transaction.
	 *
	 * @param status
	 *            the transaction object to create a savepoint for.
	 * @return the savepoint object
	 * @throws TransactionException
	 *             see {@link TransactionStatus#createSavepoint()}
	 */
	Object createSavePoint(TransactionStatus status) throws TransactionException;

	/**
	 * Executes a rollback to the given savepoint.
	 * 
	 * @param savePoint
	 *            the savepoint of the current running transaction.
	 * @throws TransactionException
	 *             see {@link TransactionStatus#rollbackToSavepoint(Object)}
	 */
	void rollbackToSavePoint(TransactionStatus status, Object savePoint) throws TransactionException;

	/**
	 * @return returns the currently active transaction or null.
	 */
	Optional<TransactionStatus> getCurrentTransaction();

}

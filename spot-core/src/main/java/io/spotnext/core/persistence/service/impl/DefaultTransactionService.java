package io.spotnext.core.persistence.service.impl;

import java.util.Optional;
import java.util.concurrent.Callable;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionUsageException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.infrastructure.support.Log;
import io.spotnext.core.persistence.service.TransactionService;

/**
 * <p>
 * DefaultTransactionService class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@SuppressFBWarnings(value = "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR", justification = "Initialized by spring post construct")
@Service
public class DefaultTransactionService extends AbstractService implements TransactionService {

	@Value("${service.persistene.transaction.timeout}")
	protected int transactionTimeoutInSec = 60;

	@Resource
	protected PlatformTransactionManager transactionManager;

	protected ThreadLocal<TransactionStatus> currentTransaction = new ThreadLocal<>();

	/**
	 * @return creates a new {@link TransactionTemplate}.
	 */
	protected TransactionTemplate createTransactionTemplate() {
		final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		transactionTemplate.setTimeout(transactionTimeoutInSec);
		transactionTemplate.setName(createTransactionName());
		return transactionTemplate;
	}

	@Override
	public <R> R execute(final Callable<R> body) throws TransactionException {
		final boolean transactionWasAlreadyActive = isTransactionActive();

		if (!transactionWasAlreadyActive) {
			start();
		}

		boolean commit = true;

		try {
			return body.call();
		} catch (final Exception e) {
			rollback(getCurrentTransaction().get());

			commit = false;

			throw new TransactionUsageException("Error during transactional execution.", e);
		} finally {
			if (!transactionWasAlreadyActive && commit) {
				if (getCurrentTransaction().isPresent() && !getCurrentTransaction().get().isCompleted()) {
					commit(getCurrentTransaction().get());
				}
			}
		}
	}

	@Override
	public void executeWithoutResult(final Runnable body) throws TransactionException {
		execute(() -> {
			body.run();
			return null;
		});
	}

	@Override
	public TransactionStatus start() throws TransactionException {
		Log.debug(String.format("Creating new transaction for thread %s (id = %s)", Thread.currentThread().getName(), Thread.currentThread().getId()));

		if (!getCurrentTransaction().isPresent()) {
			final TransactionDefinition def = createTransactionTemplate();
			final TransactionStatus status = transactionManager.getTransaction(def);

			currentTransaction.set(status);

			return status;
		} else {
			throw new CannotCreateTransactionException("There is already an active transaction.");
			// loggingService.debug("There is already a transaction running");
		}
	}

	@Override
	public Object createSavePoint() throws TransactionException {
		return createSavePoint(getCurrentTransaction().orElse(null));
	}

	@Override
	public Object createSavePoint(final TransactionStatus status) throws TransactionException {
		if (status != null && !getCurrentTransaction().get().isCompleted()) {
			return getCurrentTransaction().get().createSavepoint();
		} else {
			throw new CannotCreateTransactionException("Cannot create savepoint as there is no active transaction.");
		}
	}

	@Override
	public void commit(final TransactionStatus status) throws TransactionException {
		if (getCurrentTransaction().isPresent() && !getCurrentTransaction().get().isCompleted()) {
			transactionManager.commit(status);
			currentTransaction.remove();
		} else {
			Log.warn("Cannot commit: no transaction active.");
		}
	}

	@Override
	public void rollback(final TransactionStatus status) throws TransactionException {
		if (getCurrentTransaction().isPresent() && !getCurrentTransaction().get().isCompleted()) {
			transactionManager.rollback(status);
			currentTransaction.remove();
		} else {
			Log.warn("Cannot roleback: no transaction active.");
		}
	}

	@Override
	public void rollbackToSavePoint(final TransactionStatus status, final Object savePoint) throws TransactionException {
		if (getCurrentTransaction().isPresent() && !getCurrentTransaction().get().isCompleted()) {
			getCurrentTransaction().get().rollbackToSavepoint(savePoint);
			currentTransaction.remove();
		} else {
			throw new UnexpectedRollbackException("There is no active transaction.");
		}
	}

	@Override
	public boolean isTransactionActive() {
		return TransactionSynchronizationManager.isActualTransactionActive() || getCurrentTransaction().isPresent();
	}

	@Override
	public Optional<TransactionStatus> getCurrentTransaction() {
		return Optional.ofNullable(currentTransaction.get());
	}

	/**
	 * @return the name for the transaction, composed out of <classname of the
	 *         calling class>.<method name>.
	 */
	protected String createTransactionName() {
		final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for (final StackTraceElement e : ArrayUtils.subarray(stack, 1, stack.length)) {
			if (!this.getClass().getName().equals(e.getClassName())) {
				return e.getClassName() + "." + e.getMethodName();
			}
		}

		return null;
	}
}

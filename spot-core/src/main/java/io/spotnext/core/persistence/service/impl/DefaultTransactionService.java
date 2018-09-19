package io.spotnext.core.persistence.service.impl;

import java.util.Optional;
import java.util.concurrent.Callable;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
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
	protected int transactionTimeout = 60;

	@Resource
	protected PlatformTransactionManager transactionManager;

	/**
	 * @return creates a new {@link TransactionTemplate}.
	 */
	protected TransactionTemplate createTransactionTemplate() {
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		transactionTemplate.setTimeout(transactionTimeout);
		transactionTemplate.setName(createTransactionName());
		return transactionTemplate;
	}

	/**
	 * Starts a transaction in the given thread context. After the work has been done, either {@link #rollback()} or {@link #commit()} have to be invoked.
	 * Otherwise data might not be persisted.
	 *
	 * @throws org.springframework.transaction.TransactionException if any.
	 */
	@Override
	public TransactionStatus start() throws TransactionException {
//		return transactionManager.getTransaction(createTransactionTemplate());
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public <R> R execute(Callable<R> body) throws TransactionException {
		return createTransactionTemplate().execute(new TransactionCallback<R>() {
			@Override
			public R doInTransaction(TransactionStatus status) {
				try {
					return body.call();
				} catch (Exception e) {
					throw new IllegalTransactionStateException(e.getMessage(), e);
				}
			}
		});
	}

	/** {@inheritDoc} */
	@Override
	public void executeWithoutResult(Runnable body) throws TransactionException {
		execute(() -> {
			body.run();
			return null;
		});
	}

	/**
	 * @return the name for the transaction, composed out of <classname of the calling class>.<method name>.
	 */
	protected String createTransactionName() {
		final StackTraceElement[] stack = Thread.currentThread().getStackTrace();

		for (StackTraceElement e : ArrayUtils.subarray(stack, 1, stack.length)) {
			if (!this.getClass().getName().equals(e.getClassName())) {
				return e.getClassName() + "." + e.getMethodName();
			}
		}

		return null;
	}

	/** {@inheritDoc} */
	@Override
	public Object createSavePoint(TransactionStatus status) throws TransactionException {
		if (isTransactionActive()) {
			return status.createSavepoint();
		} else {
			throw new CannotCreateTransactionException("Cannot create savepoint as there is no active transaction.");
		}
	}

	/** {@inheritDoc} */
	@Override
	public void commit(TransactionStatus status) throws TransactionException {
		if (isTransactionActive()) {
			transactionManager.commit(status);
		} else {
			loggingService.warn("Cannot commit: no transaction active.");
		}
	}

	/** {@inheritDoc} */
	@Override
	public void rollback(TransactionStatus status) throws TransactionException {
		if (isTransactionActive()) {
			transactionManager.rollback(status);
		} else {
			loggingService.warn("Cannot roleback: no transaction active.");
		}
	}

	@Override
	public void rollbackToSavePoint(TransactionStatus status, Object savePoint) throws TransactionException {
		if (isTransactionActive()) {
			status.rollbackToSavepoint(savePoint);
		} else {
			loggingService.warn("Cannot roleback to savepoint: no transaction active.");
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean isTransactionActive() {
		return TransactionSynchronizationManager.isActualTransactionActive();
	}

	@Override
	public Optional<TransactionStatus> getCurrentTransaction() {
		if (isTransactionActive()) {
			return Optional.ofNullable(TransactionAspectSupport.currentTransactionStatus());
		}

		return Optional.empty();
	}
}

package at.spot.core.persistence.service.impl;

import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionUsageException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.persistence.service.TransactionService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value = "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR", justification = "Initialized by spring post construct")
@Service
public class DefaultTransactionService extends AbstractService implements TransactionService {

	@Resource
	protected PlatformTransactionManager transactionManager;

	protected TransactionTemplate transactionTemplate;
	protected ThreadLocal<TransactionStatus> currentTransaction = new ThreadLocal<>();

	@PostConstruct
	public void setup() {
		transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
	}

	@Override
	public <R> R execute(Callable<R> body) throws TransactionException {
		boolean transactionWasAlreadyActive = isTransactionActive();

		if (!transactionWasAlreadyActive) {
			start();
		}

		boolean commit = true;

		try {
			return body.call();
		} catch (Exception e) {
			rollback();
			commit = false;

			throw new TransactionUsageException("Error during transactional execution.", e);
		} finally {
			if (!transactionWasAlreadyActive && commit) {
				commit();
			}
		}
	}

	@Override
	public void start() throws TransactionException {
		if (currentTransaction.get() == null) {
			TransactionDefinition def = new DefaultTransactionDefinition();
			TransactionStatus status = transactionManager.getTransaction(def);

			currentTransaction.set(status);
		} else {
			throw new CannotCreateTransactionException("There is already an active transaction.");
			// loggingService.debug("There is already a transaction running");
		}
	}

	public Object createSavePoint() throws TransactionException {
		if (currentTransaction.get() != null) {
			return currentTransaction.get().createSavepoint();
		} else {
			throw new CannotCreateTransactionException("Cannot create savepoint as there is no active transaction.");
		}
	}

	@Override
	public void commit() throws TransactionException {
		if (currentTransaction.get() != null) {
			TransactionStatus status = currentTransaction.get();
			transactionManager.commit(status);
			currentTransaction.remove();
		} else {
			throw new TransactionUsageException("There is no active transaction.");
		}
	}

	@Override
	public void rollback() throws TransactionException {
		if (currentTransaction.get() != null) {
			TransactionStatus status = currentTransaction.get();
			transactionManager.rollback(status);
			currentTransaction.remove();
		} else {
			throw new UnexpectedRollbackException("There is no active transaction.");
		}
	}

	// public void rollbackToSavePoint(Object savePoint) throws TransactionException
	// {
	// if (currentTransaction.get() != null) {
	// TransactionStatus status =
	// currentTransaction.get().rollbackToSavepoint(savepoint);
	// } else {
	// throw new UnexpectedRollbackException("There is no active transaction.");
	// }
	// }

	public boolean isTransactionActive() {
		return currentTransaction.get() != null;
	}
}

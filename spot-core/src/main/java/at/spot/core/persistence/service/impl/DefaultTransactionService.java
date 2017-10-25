package at.spot.core.persistence.service.impl;

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
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.persistence.service.TransactionService;

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
	public <R> R execute(TransactionCallback<R> body) throws TransactionException {
		return transactionTemplate.execute(body);
	}

	@Override
	public void start() throws TransactionException {
		if (currentTransaction.get() == null) {
			TransactionDefinition def = new DefaultTransactionDefinition();
			TransactionStatus status = transactionManager.getTransaction(def);

			currentTransaction.set(status);
		} else {
			throw new CannotCreateTransactionException("There is already a transaction running.");
		}
	}

	@Override
	public void commit() throws TransactionException {
		if (currentTransaction.get() != null) {
			TransactionStatus status = currentTransaction.get();
			transactionManager.commit(status);
			currentTransaction.remove();
		} else {
			throw new TransactionUsageException("There is no transaction running.");
		}
	}

	@Override
	public void rollback() throws TransactionException {
		if (currentTransaction.get() != null) {
			TransactionStatus status = currentTransaction.get();
			transactionManager.rollback(status);
			currentTransaction.remove();
		} else {
			throw new UnexpectedRollbackException("There is no transaction running.");
		}
	}

}

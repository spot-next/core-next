package io.spotnext.core.persistence.service.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.annotation.logging.Log;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.infrastructure.support.LogLevel;
import io.spotnext.core.infrastructure.support.Logger;
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
//@SuppressFBWarnings(value = "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR", justification = "Initialized by spring post construct")
@Service
public class DefaultTransactionService extends AbstractService implements TransactionService {

	private final static NumberFormat NF = new DecimalFormat("0.0###");

	@Value("${service.persistence.transaction.timeout:}")
	protected int transactionTimeoutInSec = 60;

	@Autowired
	protected PlatformTransactionManager transactionManager;

	protected ThreadLocal<TransactionStatus> currentTransaction = new ThreadLocal<>();

//	@PersistenceUnit
//	protected EntityManagerFactory entityManagerFactory;

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

	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Override
	public <R> R execute(final Callable<R> body) throws TransactionException {
		final boolean transactionWasAlreadyActive = isTransactionActive();

		final TransactionStatus transaction;

		if (!transactionWasAlreadyActive) {
			transaction = start();
		} else {
			transaction = getCurrentTransaction().get();
		}

		boolean commit = true;

//		StatisticsLog stat = null;
//
//		if (Logger.isLogLevelEnabled(LogLevel.DEBUG)) {
//			stat = new StatisticsLog();
//		}

		try {
			return body.call();
		} catch (final Exception e) {
			rollback(transaction);

			commit = false;

			throw new TransactionUsageException("Error during transactional execution.", e);
		} finally {
			if (!transactionWasAlreadyActive && commit) {
				if (!transaction.isCompleted()) {
					commit(transaction);
				}
			}

//			if (stat != null) {
//				stat.log();
//			}
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
		Logger.debug(String.format("Creating new transaction for thread %s (id = %s)", Thread.currentThread().getName(), Thread.currentThread().getId()));

		if (!getCurrentTransaction().isPresent()) {
			final TransactionDefinition def = createTransactionTemplate();
			final TransactionStatus status = transactionManager.getTransaction(def);

			currentTransaction.set(status);

			return status;
		} else {
			throw new CannotCreateTransactionException("There is already an active transaction.");
			// Logger.debug("There is already a transaction running");
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
			Logger.warn("Cannot commit: no transaction active.");
		}
	}

	@Override
	public void rollback(final TransactionStatus status) throws TransactionException {
		if (getCurrentTransaction().isPresent() && !getCurrentTransaction().get().isCompleted()) {
			transactionManager.rollback(status);
			currentTransaction.remove();
		} else {
			Logger.warn("Cannot roleback: no transaction active.");
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
	 * @return the name for the transaction, composed out of <classname of the calling class>.<method name>.
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

//	private class StatisticsLog {
//		Statistics statistics;
//		private long cacheInitialHitCount;
//		private long queryInitialHitCount;
//		private long entityFetchInitialHitCount;
//		private long entityLoadInitialHitCount;
//		private long collectionFetchInitialHitCount;
//		private long collectionLoadInitialHitCount;
//		private long cacheMissInitialCount;
//		private long queryMissInitialCount;
//
//		public StatisticsLog() {
//			statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
//			statistics.setStatisticsEnabled(true);
//
//			cacheInitialHitCount = statistics.getSecondLevelCacheHitCount();
//			queryInitialHitCount = statistics.getQueryCacheHitCount();
//			entityFetchInitialHitCount = statistics.getEntityFetchCount();
//			entityLoadInitialHitCount = statistics.getEntityLoadCount();
//			collectionFetchInitialHitCount = statistics.getCollectionFetchCount();
//			collectionLoadInitialHitCount = statistics.getCollectionLoadCount();
//
//			cacheInitialHitCount = statistics.getSecondLevelCacheMissCount();
//			queryMissInitialCount = statistics.getQueryCacheMissCount();
//		}
//
//		public void log() {
//			long cacheMissCount = statistics.getSecondLevelCacheMissCount();
//
//			log("Cache", cacheInitialHitCount, statistics.getSecondLevelCacheHitCount(), cacheMissInitialCount, cacheMissCount);
//			log("Query cache", queryInitialHitCount, statistics.getQueryCacheHitCount(), queryMissInitialCount, statistics.getQueryCacheMissCount());
//			log("Entity fetch cache", entityFetchInitialHitCount, statistics.getEntityFetchCount(), cacheMissInitialCount, cacheMissCount);
//			log("Entity load cache", entityLoadInitialHitCount, statistics.getEntityLoadCount(), cacheMissInitialCount, cacheMissCount);
//			log("Collection fetch cache", collectionFetchInitialHitCount, statistics.getCollectionFetchCount(), cacheMissInitialCount, cacheMissCount);
//			log("Collection load cache", collectionLoadInitialHitCount, statistics.getCollectionLoadCount(), cacheMissInitialCount, cacheMissCount);
//		}
//
//		private void log(String name, long initialCount, long hitCount, long initialMissCount, long missCount) {
//			double ratio = (double) hitCount / (hitCount + missCount);
//
//			if (hitCount > initialCount) {
//				Logger.debug(String.format("%s - Cache HIT, Ratio=%s", name, NF.format(ratio)));
//			} else if (missCount > initialMissCount) {
//				Logger.debug(String.format("%s - Cache MISS, Ratio=%s", name, NF.format(ratio)));
//			} else {
//				Logger.debug(name + " - Cache not used");
//			}
//		}
//	}
}

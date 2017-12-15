package at.spot.core.persistence.service.impl.hibernate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnit;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.CannotCreateModelProxyException;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.persistence.service.TransactionService;
import at.spot.core.persistence.service.impl.AbstractPersistenceService;
import at.spot.core.support.util.ClassUtil;

public class HibernatePersistenceService extends AbstractPersistenceService implements PersistenceService {

	@PersistenceUnit
	protected EntityManagerFactory entityManagerFactory;

	@Resource
	protected TransactionService transactionService;

	@Resource
	protected PlatformTransactionManager transactionManager;

	@Override
	public <T extends Item> void save(final T... items) throws ModelSaveException, ModelNotUniqueException {
		save(Arrays.asList(items));
	}

	@Override
	public <T extends Item> void save(final List<T> items) throws ModelSaveException, ModelNotUniqueException {
		bindSession();

		try {
			transactionService.execute(() -> {
				for (final T item : items) {
					try {
						getSession().saveOrUpdate(item);
						// getSession().merge(item);
						getSession().flush();
					} catch (final DataIntegrityViolationException | TransactionRequiredException
							| IllegalArgumentException e) {

						throw new ModelSaveException("Could not save given items: " + e.getMessage(), e);

					} catch (final PersistenceException e) {
						final Throwable rootCause = ExceptionUtils.getRootCause(e);
						final String rootCauseMessage = rootCause != null ? rootCause.getMessage() : e.getMessage();

						throw new ModelSaveException(rootCauseMessage, e);
					}
				}

				try {
					for (final T item : items) {
						refresh(item);
					}
				} catch (final ModelNotFoundException e) {
					throw new ModelSaveException("Could not save given items", e);
				}

				return null;
			});
		} catch (final TransactionException e) {
			if (e.getCause() instanceof ModelSaveException) {
				throw (ModelSaveException) e.getCause();
			} else if (e.getCause() instanceof ModelNotUniqueException) {
				throw (ModelNotUniqueException) e.getCause();
			} else {
				throw e;
			}
		}
	}

	@Override
	public <T extends Item> T load(final Class<T> type, final long pk) throws ModelNotFoundException {
		bindSession();

		// final String query = String.format("SELECT i FROM %s i WHERE pk =
		// :pk",
		// type.getSimpleName());

		try {
			return transactionService.execute(() -> {
				return getSession().find(type, pk);
			});
		} catch (final TransactionException e) {
			if (e.getCause() instanceof ModelNotFoundException) {
				throw (ModelNotFoundException) e.getCause();
			} else {
				throw e;
			}
		}
	}

	@Override
	public <T extends Item> void refresh(final T item) throws ModelNotFoundException {
		bindSession();

		try {
			transactionService.execute(() -> {
				try {
					attach(item);

					getSession().refresh(item);
				} catch (HibernateException | TransactionRequiredException | IllegalArgumentException
						| EntityNotFoundException e) {
					throw new ModelNotFoundException(String.format("Could not refresh item with pk=%s.", item.getPk()),
							e);
				}

				return null;
			});
		} catch (final TransactionException e) {
			if (e.getCause() instanceof ModelNotFoundException) {
				throw (ModelNotFoundException) e.getCause();
			} else {
				throw e;
			}
		}
	}

	/**
	 * Attaches the given item in case it is detached.
	 * 
	 * @param item
	 * @throws ModelNotFoundException
	 */
	protected <T extends Item> void attach(final T item) throws ModelNotFoundException {
		bindSession();

		try {
			// ignore unpersisted or already attached items
			if (item.getPk() == null | getSession().contains(item)) {
				return;
			}

			final T attached = (T) getSession().load(item.getClass(), item.getPk());
			if (attached != null) {
				getSession().evict(attached);
				getSession().lock(item, LockMode.NONE);
			}
		} catch (HibernateException | TransactionRequiredException | IllegalArgumentException
				| EntityNotFoundException e) {
			throw new ModelNotFoundException(String.format("Could not refresh item with pk=%s.", item.getPk()), e);
		}
	}

	@Override
	public <T extends Item> List<T> load(final Class<T> type, final Map<String, Object> searchParameters) {
		bindSession();

		return transactionService.execute(() -> {

			TypedQuery<T> query = null;

			final CriteriaBuilder cb = getSession().getCriteriaBuilder();
			final CriteriaQuery<T> cq = cb.createQuery(type);
			final Root<T> r = cq.from(type);

			if (searchParameters != null) {
				Predicate p = cb.conjunction();

				for (final Map.Entry<String, Object> entry : searchParameters.entrySet()) {
					if (entry.getValue() instanceof Item && !((Item) entry.getValue()).isPersisted()) {
						throw new PersistenceException(String.format(
								"Passing non-persisted item as search param '%s' is not supported.", entry.getKey()));
					}

					p = cb.and(p, cb.equal(r.get(entry.getKey()), entry.getValue()));
				}

				cq.select(r).where(p);
				query = getSession().createQuery(cq);
			} else {
				query = getSession().createQuery(cq.select(r));
			}

			return query.getResultList();
		});
	}

	@Override
	public <T extends Item> List<T> load(final Class<T> type, final Map<String, Object> searchParameters,
			final Integer page, final Integer pageSize) {

		return load(type, searchParameters);
	}

	@Override
	public <T extends Item> void loadProxyModel(final T proxyItem) throws ModelNotFoundException {
		refresh(proxyItem);
	}

	@Override
	public <T extends Item> T createProxyModel(final T item) throws CannotCreateModelProxyException {
		return item;
	}

	@Override
	public <T extends Item> void remove(final T... items) {
		bindSession();

		for (final T item : items) {
			// em.remove(item);
			getSession().remove(item);
		}
	}

	@Override
	public <T extends Item> void remove(final Class<T> type, final long pk) {
		bindSession();

		// final String query = String.format("DELETE FROM %s WHERE pk IN
		// (?pk)",
		// type.getSimpleName());

		// em.createQuery(query, type).setParameter("pk", pk);

		final T item = getSession().find(type, pk);
		getSession().remove(item);
	}

	@Override
	public void saveDataStorage() {
		bindSession();

		getSession().flush();
	}

	@Override
	public void clearDataStorage() {
		bindSession();

		// em.clear();
		getSession().clear();
	}

	@Override
	public <T extends Item> void initItem(final T item) {
		for (final Field field : ClassUtil.getFieldsWithAnnotation(item.getClass(), Property.class)) {
			if (field.getType().isAssignableFrom(List.class)) {
				ClassUtil.setField(item, field.getName(), new ArrayList());
			} else if (field.getType().isAssignableFrom(Set.class)) {
				ClassUtil.setField(item, field.getName(), new HashSet());
			} else if (field.getType().isAssignableFrom(Map.class)) {
				ClassUtil.setField(item, field.getName(), new HashMap());
			}
		}
	}

	@Override
	public <T extends Item> void detach(final T... items) {
		bindSession();

		for (final T item : items) {
			// HibernateUtil.initializeObject(item, "my.app.model");

			// em.detach(item);
			getSession().detach(item);
		}
	}

	protected Session getSession() {
		return ((EntityManagerHolder) TransactionSynchronizationManager.getResource(entityManagerFactory))
				.getEntityManager().unwrap(Session.class);
	}

	protected void bindSession() {
		if (!TransactionSynchronizationManager.hasResource(entityManagerFactory)) {
			TransactionSynchronizationManager.bindResource(entityManagerFactory,
					new EntityManagerHolder(entityManagerFactory.createEntityManager()));
		}
	}

	protected void unbindSession() {
		final EntityManagerHolder emHolder = (EntityManagerHolder) TransactionSynchronizationManager
				.unbindResource(entityManagerFactory);
		EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
	}
}

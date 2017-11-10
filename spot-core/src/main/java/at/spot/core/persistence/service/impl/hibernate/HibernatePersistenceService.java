package at.spot.core.persistence.service.impl.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
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
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.CannotCreateModelProxyException;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.service.PersistenceService;

@Transactional
public class HibernatePersistenceService extends AbstractService implements PersistenceService {

	@PersistenceContext
	protected EntityManager em;

	// @Resource
	// protected SessionFactory sessionFactory;

	protected HibernateTemplate template;

	@Resource
	protected PlatformTransactionManager transactionManager;

	@Override
	public <T extends Item> void save(final T... items) throws ModelSaveException, ModelNotUniqueException {
		save(Arrays.asList(items));
	}

	@Override
	public <T extends Item> void save(final List<T> items) throws ModelSaveException, ModelNotUniqueException {

		for (final T item : items) {
			try {
				getSession().saveOrUpdate(item);
				// getSession().merge(item);
			} catch (final DataIntegrityViolationException | TransactionRequiredException
					| IllegalArgumentException e) {

				throw new ModelSaveException("Could not save given items", e);

			} catch (final PersistenceException e) {
				final Throwable rootCause = ExceptionUtils.getRootCause(e);
				final String rootCauseMessage = rootCause != null ? rootCause.getMessage() : e.getMessage();

				throw new ModelSaveException(rootCauseMessage, e);
			}
		}

		// try {
		// getSession().flush();
		//
		// for (final T item : items) {
		// refresh(item);
		// }
		// } catch (HibernateException | ModelNotFoundException e) {
		// throw new ModelSaveException("Could not save given items", e);
		// }
	}

	@Override
	public <T extends Item> T load(final Class<T> type, final long pk) throws ModelNotFoundException {
		return getSession().get(type, pk);
	}

	@Override
	public <T extends Item> void refresh(final T item) throws ModelNotFoundException {
		try {
			attach(item);
			//
			getSession().refresh(item);
			// getTemplate().refresh(entity);
		} catch (HibernateException | TransactionRequiredException | IllegalArgumentException
				| EntityNotFoundException e) {
			throw new ModelNotFoundException(String.format("Could not refresh item with pk=%s.", item.getPk()), e);
		}
	}

	/**
	 * Attaches the given item in case it is detached.
	 * 
	 * @param item
	 * @throws ModelNotFoundException
	 */
	protected <T extends Item> void attach(final T item) throws ModelNotFoundException {
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
	public <T extends Item> List<T> load(final Class<T> type, final Map<String, Comparable<?>> searchParameters) {
		TypedQuery<T> query = null;

		final CriteriaBuilder cb = getSession().getCriteriaBuilder();
		final CriteriaQuery<T> cq = cb.createQuery(type);
		final Root<T> r = cq.from(type);

		if (searchParameters != null) {
			Predicate p = cb.conjunction();

			for (final Map.Entry<String, Comparable<?>> entry : searchParameters.entrySet()) {
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
	}

	@Override
	public <T extends Item> List<T> load(final Class<T> type, final Map<String, Comparable<?>> searchParameters,
			final int page, final int pageSize, final boolean loadAsProxy) {

		return load(type, searchParameters);
	}

	@Override
	public <T extends Item> List<T> load(final Class<T> type, final Map<String, Comparable<?>> searchParameters,
			final int page, final int pageSize, final boolean loadAsProxy, final Integer minCountForParallelStream,
			final boolean returnProxies) {

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
		for (final T item : items) {
			getTemplate().delete(item);
		}
	}

	@Override
	public <T extends Item> void remove(final Class<T> type, final long pk) {
		T item;
		try {
			item = load(type, pk);
			getTemplate().delete(item);
		} catch (final ModelNotFoundException e) {
			// ignore
		}
	}

	@Override
	public void saveDataStorage() {
		getSession().flush();
	}

	@Override
	public void clearDataStorage() {
		// em.clear();
		getSession().clear();
	}

	@Override
	public <T extends Item> void initItem(final T item) {
		// for (final Field field :
		// ClassUtil.getFieldsWithAnnotation(item.getClass(), Property.class)) {
		// if (field.getType().isAssignableFrom(List.class)) {
		// ClassUtil.setField(item, field.getName(), new ArrayList());
		// } else if (field.getType().isAssignableFrom(Set.class)) {
		// ClassUtil.setField(item, field.getName(), new HashSet());
		// } else if (field.getType().isAssignableFrom(Map.class)) {
		// ClassUtil.setField(item, field.getName(), new HashMap());
		// }
		// }
	}

	@Override
	public <T extends Item> void detach(final T... items) {
		for (final T item : items) {
			// HibernateUtil.initializeObject(item, "my.app.model");

			// em.detach(item);
			getSession().detach(item);
		}
	}

	protected Session getSession() {
		return em.unwrap(Session.class);
		// return sessionFactory.getCurrentSession();
	}

	protected HibernateTemplate getTemplate() {
		if (this.template == null) {
			this.template = new HibernateTemplate(getSession().getSessionFactory());
		}

		return this.template;
	}
}

package at.spot.core.persistence.service.impl.hibernate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

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
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.CannotCreateModelProxyException;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.support.util.ClassUtil;

@Transactional
public class HibernatePersistenceService extends AbstractService implements PersistenceService {

	@PersistenceContext
	protected EntityManager em;

	// @Resource
	// protected SessionFactory sessionFactory;

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
				getSession().flush();
				getSession().refresh(item);
			} catch (final DataIntegrityViolationException | TransactionRequiredException
					| IllegalArgumentException e) {

				throw new ModelSaveException("Could not save given items", e);

			} catch (final PersistenceException e) {
				Throwable rootCause = ExceptionUtils.getRootCause(e);

				throw new ModelSaveException(rootCause.getMessage(), e);
			}
		}
	}

	@Override
	public <T extends Item> T load(final Class<T> type, final long pk) throws ModelNotFoundException {
		final String query = String.format("SELECT i FROM %s i WHERE pk = :pk", type.getSimpleName());

		return getSession().find(type, pk);

		// return em.createQuery(query, type).setParameter("pk", pk).getSingleResult();
	}

	@Override
	public <T extends Item> void refresh(final T item) throws ModelNotFoundException {
		try {
			// em.refresh(item);

			final T attached = (T) getSession().load(item.getClass(), item.getPk());
			if (attached != item) {
				getSession().evict(attached);
				getSession().lock(item, LockMode.NONE);
			}

			getSession().refresh(item);
		} catch (NullPointerException | TransactionRequiredException | IllegalArgumentException
				| EntityNotFoundException e) {
			throw new ModelNotFoundException(String.format("Could not refresh item with pk=%s.", item.getPk()), e);
		}
	}

	@Override
	public <T extends Item> Stream<T> load(final Class<T> type, final Map<String, Comparable<?>> searchParameters) {
		// String queryString = String.format("FROM %s", type.getSimpleName());
		// TypedQuery<T> query = null;
		//
		// if (searchParameters != null) {
		// final List<String> params = new ArrayList<>();
		// for (final Map.Entry<String, Comparable<?>> e :
		// searchParameters.entrySet()) {
		// params.add(e.getKey() + " = :" + e.getKey());
		// }
		//
		// queryString += String.format(" WHERE %s", StringUtils.join(params, "
		// AND "));
		// }
		//
		// query = em.createQuery(queryString, type);
		//
		// if (searchParameters != null) {
		// for (final Map.Entry<String, Comparable<?>> e :
		// searchParameters.entrySet()) {
		// query.setParameter(e.getKey(), e.getValue());
		// }
		// }

		// final CriteriaBuilder builder = em.getCriteriaBuilder();
		//
		// final CriteriaQuery<T> query =
		// em.getCriteriaBuilder().createQuery(type);
		//
		// query.from(type);

		// if (searchParameters != null) {
		// for (final Map.Entry<String, Comparable<?>> e :
		// searchParameters.entrySet()) {
		// if (e.getValue() instanceof Item) {
		// query.where(builder.equal(builder.crea y));
		// } else {
		//
		// }

		// String key = e.getKey();
		//
		// if (e.getValue() instanceof Item) {
		// key = key + ".pk";
		// }
		//
		// params.add(e.getKey() + " = :" + e.getKey());
		//// }
		// }

		// return query.getResultList().stream();

		// // Query by example
		// final Session session = getEntitySession();
		// // create an example from our customer, exclude all zero valued
		// numeric
		// // properties
		// final Example customerExample = Example.create().excludeZeroes();
		// // create criteria based on the customer example
		// final Criteria criteria =
		// session.createCriteria(Customer.class).add(customerExample);
		// // perform the query
		// criteria.list();

		TypedQuery<T> query = null;

		final CriteriaBuilder cb = getSession().getCriteriaBuilder();
		final CriteriaQuery<T> cq = cb.createQuery(type);
		final Root<T> r = cq.from(type);

		if (searchParameters != null) {
			Predicate p = cb.conjunction();

			final Metamodel mm = getSession().getMetamodel();
			final EntityType<T> et = mm.entity(type);

			for (final Map.Entry<String, Comparable<?>> entry : searchParameters.entrySet()) {
				if (entry.getValue() instanceof Item && !getSession().contains(entry.getValue())) {
					throw new PersistenceException(String
							.format("Passing non-persisted item as search param %s is not supported.", entry.getKey()));
				}

				p = cb.and(p, cb.equal(r.get(entry.getKey()), entry.getValue()));
			}

			// for (final Attribute<? super T, ?> attr : et.getAttributes()) {
			// final String name = attr.getName();
			// final String javaName = attr.getJavaMember().getName();
			// final String getter = "get" + javaName.substring(0,
			// 1).toUpperCase()
			// + javaName.substring(1);
			// final Method m = cl.getMethod(getter, (Class<?>[]) null);
			//
			// if (m.invoke(example, (Object[]) null) != null)
			// p = cb.and(p, cb.equal(r.get(name), m.invoke(example, (Object[])
			// null)));
			// }

			cq.select(r).where(p);
			query = getSession().createQuery(cq);
		} else {
			query = getSession().createQuery(cq.select(r));
		}

		return query.getResultList().stream();
	}

	// protected <T extends Item> boolean isPersisted(final T item) {
	// return em.contains(item);
	// }

	@Override
	public <T extends Item> Stream<T> load(final Class<T> type, final Map<String, Comparable<?>> searchParameters,
			final int page, final int pageSize, final boolean loadAsProxy) {

		return load(type, searchParameters);
	}

	@Override
	public <T extends Item> Stream<T> load(final Class<T> type, final Map<String, Comparable<?>> searchParameters,
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
			// em.remove(item);
			getSession().remove(item);
		}
	}

	@Override
	public <T extends Item> void remove(final Class<T> type, final long pk) {
		final String query = String.format("DELETE FROM %s WHERE pk IN (?pk)", type.getSimpleName());

		// em.createQuery(query, type).setParameter("pk", pk);

		T item = getSession().find(type, pk);
		getSession().remove(item);
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
		for (Field field : ClassUtil.getFieldsWithAnnotation(item.getClass(), Property.class)) {
			if (field.getType().isAssignableFrom(List.class)) {
				ClassUtil.setField(item, field.getName(), new ArrayList());
			} else if (field.getType().isAssignableFrom(Set.class)) {
				ClassUtil.setField(item, field.getName(), new HashSet());
			} else if (field.getType().isAssignableFrom(Map.class)) {
				ClassUtil.setField(item, field.getName(), new HashMap());
			}
		}

		// if (item != null && item.getPk() != null) {
		// try {
		// refresh(item);
		// } catch (ModelNotFoundException e) {
		// loggingService.warn(String.format("Could not initialize item with
		// pk=%s",
		// item.getPk()));
		// }
		// } else {
		// loggingService.warn("Could not initialize null item");
		// }
		// not needed
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

}

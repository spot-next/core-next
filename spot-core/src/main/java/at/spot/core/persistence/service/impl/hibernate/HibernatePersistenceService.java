package at.spot.core.persistence.service.impl.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.persistence.EntityExistsException;
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
import javax.transaction.Transactional;

import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

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

	@Override
	public <T extends Item> void save(final T... items) throws ModelSaveException, ModelNotUniqueException {
		save(Arrays.asList(items));
	}

	@Override
	public <T extends Item> void save(final List<T> items) throws ModelSaveException, ModelNotUniqueException {
		for (final T item : items) {
			try {
				Session session = em.unwrap(Session.class);

				session.saveOrUpdate(item);
			} catch (final ConstraintViolationException | EntityExistsException e) {
				throw new ModelNotUniqueException(e);
			} catch (final TransactionRequiredException | IllegalArgumentException e) {
				throw new ModelSaveException(e);
			} catch (PersistenceException e) {
				throw new ModelSaveException(e);
			}
		}
	}

	@Override
	public <T extends Item> T load(final Class<T> type, final long pk) throws ModelNotFoundException {
		final String query = String.format("SELECT i FROM %s i WHERE pk = :pk", type.getSimpleName());

		return em.createQuery(query, type).setParameter("pk", pk).getSingleResult();
	}

	@Override
	public <T extends Item> void refresh(final T item) throws ModelNotFoundException {
		try {
			// em.refresh(item);

			final T attached = (T) getEntitySession().load(item.getClass(), item.getPk());
			if (attached != item) {
				getEntitySession().evict(attached);
				getEntitySession().lock(item, LockMode.NONE);
			}

			getEntitySession().refresh(item);
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

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<T> cq = cb.createQuery(type);
		final Root<T> r = cq.from(type);

		if (searchParameters != null) {
			Predicate p = cb.conjunction();

			final Metamodel mm = em.getMetamodel();
			final EntityType<T> et = mm.entity(type);

			for (final Map.Entry<String, Comparable<?>> entry : searchParameters.entrySet()) {
				if (entry.getValue() instanceof Item && !em.contains(entry.getValue())) {
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
			query = em.createQuery(cq);
		} else {
			query = em.createQuery(cq.select(r));
		}

		return query.getResultList().stream();
	}

	protected <T extends Item> boolean isPersisted(final T item) {
		return em.contains(item);
	}

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
			em.remove(item);
		}
	}

	@Override
	public <T extends Item> void remove(final Class<T> type, final long pk) {
		final String query = String.format("DELETE FROM %s WHERE pk IN (?pk)", type.getSimpleName());

		em.createQuery(query, type).setParameter("pk", pk);
	}

	@Override
	public void saveDataStorage() {
		em.flush();
	}

	@Override
	public void clearDataStorage() {
		em.clear();
	}

	@Override
	public <T extends Item> void initItem(final T item) {
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

			em.detach(item);
		}
	}

	protected Session getEntitySession() {
		return em.unwrap(Session.class);
	}

}

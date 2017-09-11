package at.spot.core.persistence.service.impl.hibernate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.LockMode;
import org.hibernate.Session;

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
				em.persist(item);
			} catch (final EntityExistsException e) {
				throw new ModelNotUniqueException(e);
			} catch (final IllegalArgumentException e) {
				throw new ModelSaveException(e);
			}
		}
	}

	@Override
	public <T extends Item> T load(final Class<T> type, final long pk) throws ModelNotFoundException {
		final String query = String.format("SELECT i FROM %s i WHERE pk = ?pk", type.getSimpleName());

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
		String queryString = String.format("FROM %s", type.getSimpleName());
		TypedQuery<T> query = null;

		if (searchParameters != null) {
			final List<String> params = new ArrayList<>();
			for (final Map.Entry<String, Comparable<?>> e : searchParameters.entrySet()) {
				params.add(e.getKey() + " = :" + e.getKey());
			}

			queryString += String.format(" WHERE %s", StringUtils.join(params, " AND "));
		}

		query = em.createQuery(queryString, type);

		if (searchParameters != null) {
			for (final Map.Entry<String, Comparable<?>> e : searchParameters.entrySet()) {
				query.setParameter(e.getKey(), e.getValue());
			}
		}

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

	protected Session getEntitySession() {
		return em.unwrap(Session.class);
	}

}

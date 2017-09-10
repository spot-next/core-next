package at.spot.core.persistence.service.impl.hibernate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;

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
	public <T extends Item> void save(T... items) throws ModelSaveException, ModelNotUniqueException {
		save(Arrays.asList(items));
	}

	@Override
	public <T extends Item> void save(List<T> items) throws ModelSaveException, ModelNotUniqueException {
		for (T item : items) {
			em.persist(item);
		}
	}

	@Override
	public <T extends Item> T load(Class<T> type, long pk) throws ModelNotFoundException {
		String query = String.format("SELECT i FROM %s i WHERE pk = ?pk", type.getSimpleName());

		return em.createQuery(query, type).setParameter("pk", pk).getSingleResult();
	}

	@Override
	public <T extends Item> void refresh(T item) throws ModelNotFoundException {
		try {
			em.refresh(item);
		} catch (TransactionRequiredException | IllegalArgumentException | EntityNotFoundException e) {
			throw new ModelNotFoundException(String.format("Could not refresh item with pk=%s.", item.getPk()), e);
		}
	}

	@Override
	public <T extends Item> Stream<T> load(Class<T> type, Map<String, Comparable<?>> searchParameters) {
		List<String> params = new ArrayList<>();
		for (Map.Entry<String, Comparable<?>> e : searchParameters.entrySet()) {
			params.add(e.getKey() + " = :" + e.getKey());
		}

		String query = String.format("FROM %s WHERE %s", type.getSimpleName(), StringUtils.join(params, " AND "));

		TypedQuery<T> qry = em.createQuery(query, type);

		for (Map.Entry<String, Comparable<?>> e : searchParameters.entrySet()) {
			qry.setParameter(e.getKey(), e.getValue());
		}

		return qry.getResultList().stream();
	}

	@Override
	public <T extends Item> Stream<T> load(Class<T> type, Map<String, Comparable<?>> searchParameters, int page,
			int pageSize, boolean loadAsProxy) {

		return load(type, searchParameters);
	}

	@Override
	public <T extends Item> Stream<T> load(Class<T> type, Map<String, Comparable<?>> searchParameters, int page,
			int pageSize, boolean loadAsProxy, Integer minCountForParallelStream, boolean returnProxies) {

		return load(type, searchParameters);
	}

	@Override
	public <T extends Item> void loadProxyModel(T proxyItem) throws ModelNotFoundException {
		refresh(proxyItem);
	}

	@Override
	public <T extends Item> T createProxyModel(T item) throws CannotCreateModelProxyException {
		return item;
	}

	@Override
	public <T extends Item> void remove(T... items) {
		for (T item : items) {
			em.remove(item);
		}
	}

	@Override
	public <T extends Item> void remove(Class<T> type, long pk) {
		String query = String.format("DELETE FROM %s WHERE pk IN (?pk)", type.getSimpleName());

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
	public <T extends Item> void initItem(T item) {
		if (item != null && item.getPk() != null) {
			try {
				refresh(item);
			} catch (ModelNotFoundException e) {
				loggingService.warn(String.format("Could not initialize item with pk=%s", item.getPk()));
			}
		} else {
			loggingService.warn("Could not initialize null item");
		}
	}

}

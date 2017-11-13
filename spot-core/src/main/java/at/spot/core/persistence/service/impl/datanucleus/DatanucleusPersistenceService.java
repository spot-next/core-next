package at.spot.core.persistence.service.impl.datanucleus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.CannotCreateModelProxyException;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.persistence.service.impl.AbstractPersistenceService;

@Transactional
public class DatanucleusPersistenceService extends AbstractPersistenceService implements PersistenceService {

	@Resource
	protected PlatformTransactionManager transactionManager;

	@PersistenceContext
	protected EntityManager em;

	@Override
	public <T extends Item> void save(T... models) throws ModelSaveException, ModelNotUniqueException {
		save(Arrays.asList(models));
	}

	@Override
	public <T extends Item> void save(List<T> models) throws ModelSaveException, ModelNotUniqueException {
		for (T item : models) {
			em.persist(item);
		}
	}

	@Override
	public <T extends Item> T load(Class<T> type, long pk) throws ModelNotFoundException {
		return em.find(type, pk);
	}

	@Override
	public <T extends Item> void refresh(T item) throws ModelNotFoundException {
		em.refresh(item);
	}

	@Override
	public <T extends Item> List<T> load(Class<T> type, Map<String, Object> searchParameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Item> List<T> load(Class<T> type, Map<String, Object> searchParameters, Integer page,
			Integer pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Item> void loadProxyModel(T proxyItem) throws ModelNotFoundException {
		em.refresh(proxyItem);
	}

	@Override
	public <T extends Item> T createProxyModel(T item) throws CannotCreateModelProxyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Item> void remove(T... items) {
		for (T item : items) {
			em.remove(item);
		}
	}

	@Override
	public <T extends Item> void remove(Class<T> type, long pk) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveDataStorage() {
		em.flush();
	}

	@Override
	public void clearDataStorage() {

	}

	@Override
	public <T extends Item> void initItem(T item) {
		// TODO Auto-generated method stub
	}

	@Override
	public <T extends Item> void detach(T... items) {
		for (T item : items) {
			em.detach(item);
		}
	}
}

package at.spot.core.infrastructure.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.type.PK;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.support.util.ELParser;

@Service
public class DefaultModelService extends AbstractModelService {

	@Autowired
	protected PersistenceService persistenceService;

	@Override
	public <T extends Item> void save(T model) throws ModelSaveException, ModelNotUniqueException {
		persistenceService.save(model);
	}

	@Override
	public <T extends Item> void saveAll(T... items) throws ModelSaveException, ModelNotUniqueException {
		persistenceService.save(items);
	}

	@Override
	public <T extends Item> void saveAll(List<T> models) throws ModelSaveException, ModelNotUniqueException {
		persistenceService.save(models);
	}

	@Override
	public <T extends Item> T get(Class<T> type, Map<String, Comparable<?>> searchParameters)
			throws ModelNotFoundException {
		List<T> items = getAll(type, searchParameters);

		return items.size() > 0 ? items.get(0) : null;
	}

	@Override
	public <T extends Item> List<T> getAll(Class<T> type, Map<String, Comparable<?>> searchParameters)
			throws ModelNotFoundException {

		return persistenceService.load(type, searchParameters);
	}

	@Override
	public <T extends Item> T get(PK pk) throws ModelNotFoundException {
		return get((Class<T>) pk.getType(), pk.longValue());
	}

	@Override
	public <T extends Item> T get(Class<T> type, long pk) throws ModelNotFoundException {
		return (T) persistenceService.load(type, pk);
	}

	@Override
	public <T extends Item> void loadProxyModel(T proxyItem) throws ModelNotFoundException {
		persistenceService.loadProxyModel(proxyItem);
	}

	@Override
	public <T extends Item> Object getPropertyValue(T item, String propertyName) {
		ELParser transformer = new ELParser(propertyName);

		return transformer.transform(item);
	}

	@Override
	public <T extends Item, V> V getPropertyValue(T item, String propertyName, Class<V> type) {
		// TODO Auto-generated method stub
		return (V) getPropertyValue(item, propertyName);
	}

	@Override
	public <T extends Item> void refresh(T item) throws ModelNotFoundException {
		persistenceService.refresh(item);
	}

	@Override
	public void remove(PK pk) {
		persistenceService.remove(pk);
	}

	@Override
	public <T extends Item> void remove(Class<T> type, long pk) {
		persistenceService.remove(new PK(pk, type));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Item> void remove(T item) {
		persistenceService.remove(item);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Item> void remove(T... items) {
		persistenceService.remove(items);
	}
}

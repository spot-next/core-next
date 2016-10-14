package at.spot.core.infrastructure.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.data.model.Item;
import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.type.PK;
import at.spot.core.infrastructure.util.ELParser;
import at.spot.core.persistence.service.PersistenceService;

@Service
public class DefaultModelService extends AbstractModelService {

	@Autowired
	protected PersistenceService persistenceService;

	@Override
	public <T extends Item> void save(T model) throws ModelSaveException {
		persistenceService.save(model);
	}

	@Override
	public <T extends Item> void saveAll(T... models) throws ModelSaveException {
		persistenceService.saveAll(models);
	}

	@Override
	public <T extends Item> void saveAll(List<T> models) throws ModelSaveException {
		persistenceService.saveAll(models);
	}

	@Override
	public <T extends Item> List<T> get(Class<T> type, Map<String, Object> searchParameters)
			throws ModelNotFoundException {

		return persistenceService.load(type, searchParameters);
	}

	@Override
	public <T extends Item> T get(Class<T> type, PK pk) throws ModelNotFoundException {
		return get(type, pk.longValue());
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
		Transformer transformer = new ELParser(propertyName);

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
}

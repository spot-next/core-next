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
	public <T extends Item> void save(final T model) throws ModelSaveException, ModelNotUniqueException {
		persistenceService.save(model);
	}

	@Override
	public <T extends Item> void saveAll(final T... items) throws ModelSaveException, ModelNotUniqueException {
		persistenceService.save(items);
	}

	@Override
	public <T extends Item> void saveAll(final List<T> models) throws ModelSaveException, ModelNotUniqueException {
		persistenceService.save(models);
	}

	@Override
	public <T extends Item> T get(final Class<T> type, final Map<String, Comparable<?>> searchParameters) {

		// ignore empty search parameters
		if (searchParameters == null || searchParameters.values().size() == 0) {
			return null;
		}

		final List<T> items = getAll(type, searchParameters);

		return items.size() > 0 ? items.get(0) : null;
	}

	@Override
	public <T extends Item> List<T> getAll(final Class<T> type, final Map<String, Comparable<?>> searchParameters) {
		return persistenceService.load(type, searchParameters);
	}

	@Override
	public <T extends Item> List<T> getAll(final Class<T> type, final Map<String, Comparable<?>> searchParameters,
			final long start, final long amount, final boolean loadAsProxy) {

		return persistenceService.load(type, searchParameters, start, amount, loadAsProxy);
	}

	@Override
	public <T extends Item> List<T> getAll(final Class<T> type) {

		return persistenceService.load(type, null);
	}

	@Override
	public <T extends Item> T get(final Class<T> type, final long pk) throws ModelNotFoundException {
		return persistenceService.load(type, pk);
	}

	@Override
	public <T extends Item> void loadProxyModel(final T proxyItem) throws ModelNotFoundException {
		persistenceService.loadProxyModel(proxyItem);
	}

	@Override
	public <T extends Item> Object getPropertyValue(final T item, final String propertyName) {
		final ELParser transformer = new ELParser(propertyName);

		return transformer.transform(item);
	}

	@Override
	public <T extends Item, V> V getPropertyValue(final T item, final String propertyName, final Class<V> type) {
		// TODO Auto-generated method stub
		return (V) getPropertyValue(item, propertyName);
	}

	@Override
	public <T extends Item> void refresh(final T item) throws ModelNotFoundException {
		persistenceService.refresh(item);
	}

	@Override
	public <T extends Item> void remove(final Class<T> type, final long pk) {
		persistenceService.remove(new PK(pk, type));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Item> void remove(final T item) {
		persistenceService.remove(item);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Item> void remove(final T... items) {
		persistenceService.remove(items);
	}

}

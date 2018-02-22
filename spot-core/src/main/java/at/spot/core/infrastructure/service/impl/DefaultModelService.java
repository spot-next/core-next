package at.spot.core.infrastructure.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.exception.ModelValidationException;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.support.util.ClassUtil;
import at.spot.core.support.util.ValidationUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
@Service
public class DefaultModelService extends AbstractModelService {

	@Override
	public <T extends Item> void save(final T model)
			throws ModelSaveException, ModelNotUniqueException, ModelValidationException {

		saveAll(model);
	}

	@Override
	public <T extends Item> void saveAll(final T... models)
			throws ModelSaveException, ModelNotUniqueException, ModelValidationException {

		saveAll(Arrays.asList(models));
	}

	@Override
	public <T extends Item> void saveAll(final List<T> models)
			throws ModelSaveException, ModelNotUniqueException, ModelValidationException {

		super.applyPrepareInterceptors(models);
		super.validateModels(models);

		persistenceService.save(models);
	}

	@Override
	public <T extends Item> T getByExample(final T example) {
		final Map<String, Object> map = persistenceService.convertItemToMap(example);

		ValidationUtil.validateMinSize("Example item has no properties set", map.values(), 1);

		return (T) get(example.getClass(), map);
	}

	@Override
	public <T extends Item> T get(final Class<T> type, final Map<String, Object> searchParameters) {
		// ignore empty search parameters
		if (searchParameters == null || searchParameters.values().size() == 0) {
			return null;
		}

		final List<T> items = getAll(type, searchParameters);

		return items.size() > 0 ? items.get(0) : null;
	}

	@Override
	public <T extends Item> List<T> getAll(final Class<T> type, final Map<String, Object> searchParameters) {
		return getAll(type, searchParameters, 0, 0);
	}

	@Override
	public <T extends Item> List<T> getAll(final Class<T> type, final Map<String, Object> searchParameters,
			final int page, final int pageSize) {

		return persistenceService.load(type, searchParameters, page, pageSize);
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
	public <T extends Item> void refresh(final T item) throws ModelNotFoundException {
		if (item == null || item.getPk() == null) {
			throw new ModelNotFoundException("Given item is null");
		}

		persistenceService.refresh(Collections.singletonList(item));
	}

	@Override
	public <T extends Item> void remove(final Class<T> type, final long pk) {
		persistenceService.remove(type, pk);
	}

	@Override
	public <T extends Item> void removeAll(List<T> items) throws ModelNotFoundException {
		persistenceService.remove(items);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Item> void remove(final T... items) throws ModelNotFoundException {
		removeAll(Arrays.asList(items));
	}

	@Override
	public <T extends Item> Object getPropertyValue(final T item, final String propertyName) {
		return getPropertyValue(item, propertyName, Item.class);
	}

	@Override
	public <T extends Item, V> V getPropertyValue(final T item, final String propertyName, final Class<V> valueType) {
		// final ELParser<T, V> transformer = new ELParser<>(propertyName);
		//
		// return transformer.transform(item);

		return (V) ClassUtil.getField(item, propertyName, true);
	}

	@Override
	public <T extends Item> void setPropertyValue(final T item, final String propertyName, final Object propertyValue) {
		ClassUtil.setField(item, propertyName, propertyValue);
	}
}

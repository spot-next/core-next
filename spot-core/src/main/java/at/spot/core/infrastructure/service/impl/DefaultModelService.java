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
	public <T extends Item> T get(final Class<T> type, final long pk) throws ModelNotFoundException {
		final T item = persistenceService.load(type, pk);
		applyLoadInterceptors(Collections.singletonList(item));

		return item;
	}

	@Override
	public <T extends Item> T get(final Class<T> type, final Map<String, Object> searchParameters) {
		final List<T> items = getAllInternal(type, searchParameters, 0, 0);

		if (items.size() > 1) {
			loggingService.warn(String.format("Found more than one matching item for the given search parameters: %s ",
					searchParameters));
		}

		if (items.size() == 1) {
			applyLoadInterceptors(Collections.singletonList(items.get(0)));
			return items.get(0);
		}

		return null;
	}

	@Override
	public <T extends Item> List<T> getAll(final Class<T> type, final Map<String, Object> searchParameters) {
		return getAll(type, searchParameters, 0, 0);
	}

	@Override
	public <T extends Item> List<T> getAll(final Class<T> type, final Map<String, Object> searchParameters,
			final int page, final int pageSize) {

		final List<T> items = getAllInternal(type, searchParameters, page, pageSize);
		applyLoadInterceptors(items);

		return items;
	}

	protected <T extends Item> List<T> getAllInternal(final Class<T> type, final Map<String, Object> searchParameters,
			final int page, final int pageSize) {

		// return nothing if search parameters are empty
		if (searchParameters == null || searchParameters.values().size() == 0) {
			return Collections.emptyList();
		}

		final List<T> items = persistenceService.load(type, searchParameters, page, pageSize);

		return items;
	}

	@Override
	public <T extends Item> List<T> getAll(final Class<T> type) {
		final List<T> items = persistenceService.load(type, null);

		applyLoadInterceptors(items);

		return items;
	}

	@Override
	public <T extends Item> T getByExample(final T example) {
		final Map<String, Object> map = persistenceService.convertItemToMap(example);

		ValidationUtil.validateMinSize("Example item has no properties set", map.values(), 1);

		T item = (T) get(example.getClass(), map);

		return item;
	}

	@Override
	public <T extends Item> List<T> getAllByExample(final T example) {
		final Map<String, Object> map = persistenceService.convertItemToMap(example);

		ValidationUtil.validateMinSize("Example item has no properties set", map.values(), 1);

		List<T> items = (List<T>) getAll(example.getClass(), map, 0, 0);

		return items;
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
		// TODO: remove interceptors

		persistenceService.remove(type, pk);
	}

	@Override
	public <T extends Item> void removeAll(List<T> items) throws ModelNotFoundException {
		applyRemoveInterceptors(items);

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

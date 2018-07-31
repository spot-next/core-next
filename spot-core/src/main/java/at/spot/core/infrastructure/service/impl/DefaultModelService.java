package at.spot.core.infrastructure.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

import at.spot.core.persistence.query.ModelQuery;

import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.exception.ModelValidationException;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.support.util.ClassUtil;
import at.spot.core.support.util.ValidationUtil;
import at.spot.core.types.Item;
import at.spot.core.types.Localizable;
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

		super.setUserInformation(models);
		super.applyPrepareInterceptors(models);
		super.validateModels(models);

		persistenceService.save(models);
	}

	@Override
	public <T extends Item> T get(final Class<T> type, final long pk) throws ModelNotFoundException {
		final T item = persistenceService.load(type, pk);

		if (item != null) {
			applyLoadInterceptors(Collections.singletonList(item));
		}

		return item;
	}

	@Override
	public <T extends Item> T get(final Class<T> type, final Map<String, Object> searchParameters)
			throws ModelNotUniqueException {

		return get(new ModelQuery<>(type, searchParameters));
	}

	@Override
	public <T extends Item> T get(final ModelQuery<T> query) throws ModelNotUniqueException {
		final List<T> items = getAllInternal(query);

		if (items.size() > 1) {
			throw new ModelNotUniqueException("Found more than 1 result for the given search parameters");
		}

		if (items.size() > 0) {
			applyLoadInterceptors(Collections.singletonList(items.get(0)));
			return items.get(0);
		}

		return null;
	}

	@Override
	public <T extends Item> List<T> getAll(final Class<T> type, final Map<String, Object> searchParameters) {
		return getAll(new ModelQuery<>(type, searchParameters));
	}

	@Override
	public <T extends Item> List<T> getAll(final ModelQuery<T> query) {
		final List<T> items = getAllInternal(query);

		applyLoadInterceptors(items);

		return items;
	}

	protected <T extends Item> List<T> getAllInternal(final ModelQuery<T> query) {
		final List<T> items = persistenceService.load(query);

		return items;
	}

	@Override
	public <T extends Item> T getByExample(final T example) {
		final Map<String, Object> map = persistenceService.convertItemToMap(example);

		ValidationUtil.validateMinSize("Example item has no properties set", map.values(), 1);

		final T item = get(new ModelQuery<T>((Class<T>) example.getClass(), map));

		return item;
	}

	@Override
	public <T extends Item> List<T> getAllByExample(final T example) {
		final Map<String, Object> map = persistenceService.convertItemToMap(example);

		ValidationUtil.validateMinSize("Example item has no properties set", map.values(), 1);

		final List<T> items = getAll(new ModelQuery<T>((Class<T>) example.getClass(), map));

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
	public <T extends Item> void removeAll(final List<T> items) throws ModelNotFoundException {
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
	public <T extends Item, V> V getLocalizedPropertyValue(T item, String propertyName, Class<V> valueType,
			Locale locale) {

		final Localizable<V> localizable = (Localizable<V>) ClassUtil.getField(item, propertyName, true);

		if (localizable != null) {
			return localizable.get(locale);
		} else {
			loggingService.warn(String.format("Localized property %s on type %s not found", propertyName,
					item.getClass().getSimpleName()));
		}

		return null;
	}

	@Override
	public <T extends Item, V> V getPropertyValue(final T item, final String propertyName, final Class<V> valueType) {
		return (V) ClassUtil.getField(item, propertyName, true);
	}

	@Override
	public <T extends Item> void setLocalizedPropertyValue(T item, String propertyName, Object propertyValue,
			Locale locale) {

		final Localizable<Object> localizable = (Localizable<Object>) ClassUtil.getField(item, propertyName, true);

		if (localizable != null) {
			localizable.set(locale, propertyValue);
		} else {
			loggingService.warn(String.format("Localized property %s on type %s not found", propertyName,
					item.getClass().getSimpleName()));
		}
	}

	@Override
	public <T extends Item> void setPropertyValue(final T item, final String propertyName, final Object propertyValue) {
		ClassUtil.setField(item, propertyName, propertyValue);
	}
}

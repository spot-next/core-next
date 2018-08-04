package at.spot.core.infrastructure.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import at.spot.core.infrastructure.exception.ModelCreationException;
import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.exception.ModelValidationException;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.query.ModelQuery;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.types.Item;

public interface ModelService {
	/**
	 * Creates an unsaved instance of the given type.
	 */
	<T extends Item> T create(Class<T> type) throws ModelCreationException;

	/**
	 * Saves the given model. Referenced Item models will not be saved.
	 */
	<T extends Item> void save(T model) throws ModelSaveException, ModelNotUniqueException, ModelValidationException;

	/**
	 * Saves all the given Item models. Referenced Item models will not be
	 * saved.
	 */
	@SuppressWarnings("unchecked")
	<T extends Item> void saveAll(T... items)
			throws ModelSaveException, ModelNotUniqueException, ModelValidationException;

	/**
	 * Saves all the given Item models. Referenced Item models will not be
	 * saved.
	 */
	<T extends Item> void saveAll(List<T> models)
			throws ModelSaveException, ModelNotUniqueException, ModelValidationException;

	/**
	 * Returns an {@link Item} based on its PK.
	 */
	<T extends Item> T get(Class<T> type, long pk) throws ModelNotFoundException;

	/**
	 * Returns the first {@link Item} based on the given search parameters (key
	 * = property name, value = property value).
	 * 
	 * @throws ModelNotUniqueException
	 *             if there is more than one matching item.
	 */
	<T extends Item> T get(Class<T> type, Map<String, Object> searchParameters) throws ModelNotUniqueException;

	/**
	 * Returns the first {@link Item} based on the given model query parameters.
	 * the {@link ModelQuery} allows you to set the fetching strategy,
	 * pagination and to limit the result.
	 * 
	 * @throws ModelNotUniqueException
	 *             if there is more than one matching item.
	 */
	<T extends Item> T get(ModelQuery<T> query) throws ModelNotUniqueException;;

	/**
	 * Returns an object based on the given search parameters (key = property
	 * name, value = property value).
	 * 
	 * @param searchParameters
	 *            if empty or null, all items of the given type will be
	 *            returned.
	 */
	<T extends Item> List<T> getAll(Class<T> type, Map<String, Object> searchParameters);

	/**
	 * Returns an object based on the given search parameters (key = property
	 * name, value = property value).
	 */
	<T extends Item> List<T> getAll(ModelQuery<T> query);

	/**
	 * Returns the first {@link Item} based on the given example item.
	 * 
	 * @param example
	 *            the example item instance.
	 */
	<T extends Item> T getByExample(T example);

	/**
	 * Returns all {@link Item}s that match the given example item.
	 * 
	 * @param example
	 *            the example item instance.
	 */
	<T extends Item> List<T> getAllByExample(T example);

	/**
	 * Removes the given item.
	 */
	<T extends Item> void remove(Class<T> type, long pk) throws ModelNotFoundException;

	/**
	 * Removes the given items.
	 */
	<T extends Item> void removeAll(List<T> items) throws ModelNotFoundException;

	/**
	 * Removes the given item.
	 */
	@SuppressWarnings("unchecked")
	<T extends Item> void remove(T... items) throws ModelNotFoundException;

	/**
	 * Refreshes the given model's properties.
	 */
	<T extends Item> void refresh(T item) throws ModelNotFoundException;

	/**
	 * Returns the item's value of the given property.
	 */
	<T extends Item> Object getPropertyValue(T item, String propertyName);

	/**
	 * Returns the item's value of the given property.
	 * 
	 * @param item
	 * @param propertyName
	 * @param valueType
	 */
	<T extends Item, V> V getPropertyValue(T item, String propertyName, Class<V> valueType);

	/**
	 * Returns the item's value of the given property.
	 * 
	 * @param item
	 * @param propertyName
	 * @param valueType
	 * @param locale
	 */
	<T extends Item, V> V getLocalizedPropertyValue(T item, String propertyName, Class<V> valueType, Locale locale);

	/**
	 * Sets the {@link Item}'s property to the given value.
	 * 
	 * @param item
	 * @param propertyName
	 * @param propertyValue
	 */
	<T extends Item> void setPropertyValue(T item, String propertyName, Object propertyValue);

	/**
	 * Sets the {@link Item}'s property to the given value.
	 * 
	 * @param item
	 * @param propertyName
	 * @param propertyValue
	 * @param locale
	 */
	<T extends Item> void setLocalizedPropertyValue(T item, String propertyName, Object propertyValue, Locale locale);

	/**
	 * Detaches the given item models from the persistence context.
	 * 
	 * @see PersistenceService#detach(List)
	 * 
	 * @param items
	 */
	<T extends Item> void detach(T... items);

	/**
	 * @see ModelService#detach(Item...)
	 * 
	 * @param items
	 */
	<T extends Item> void detach(List<T> items);
}

package at.spot.core.infrastructure.service;

import java.util.List;
import java.util.Map;

import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.exception.ModelValidationException;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.service.PersistenceService;

public interface ModelService {

	/**
	 * Creates a new proxy item that references the given item.
	 * 
	 * @param pk
	 * @return
	 */
	<T extends Item> T createProxyModel(Class<T> itemType, long pk);

	/**
	 * Creates a new proxy item that references the given item.
	 * 
	 * @param pk
	 * @return
	 */
	<T extends Item> T createProxyModel(T item);

	/**
	 * Creates an unsaved instance of the given type.
	 * 
	 * @param type
	 * @return
	 */
	<T extends Item> T create(Class<T> type);

	/**
	 * Saves the given model. Referenced Item models will not be saved.
	 * 
	 * @param type
	 * @return
	 */
	<T extends Item> void save(T model) throws ModelSaveException, ModelNotUniqueException, ModelValidationException;

	/**
	 * Saves all the given Item models. Referenced Item models will not be saved.
	 * 
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	<T extends Item> void saveAll(T... items)
			throws ModelSaveException, ModelNotUniqueException, ModelValidationException;

	/**
	 * Saves all the given Item models. Referenced Item models will not be saved.
	 * 
	 * @param type
	 * @return
	 */
	<T extends Item> void saveAll(List<T> models)
			throws ModelSaveException, ModelNotUniqueException, ModelValidationException;

	/**
	 * Returns an {@link Item} based on its PK.
	 * 
	 * @param pk
	 * @return
	 */
	<T extends Item> T get(Class<T> type, long pk) throws ModelNotFoundException;

	/**
	 * Returns the first {@link Item} based on the given search parameters (key =
	 * property name, value = property value).
	 * 
	 * @param searchParameters
	 * @return
	 */
	<T extends Item> T get(Class<T> type, Map<String, Comparable<?>> searchParameters);

	/**
	 * Returns the first {@link Item} based on the given example item.
	 * 
	 * @param example
	 *            the example item instance.
	 * @return
	 */
	<T extends Item> T get(Class<T> type, T example) throws ModelValidationException;

	/**
	 * Removes the given item.
	 * 
	 * @param item
	 */
	<T extends Item> void remove(Class<T> type, long pk) throws ModelNotFoundException;

	/**
	 * Removes the given item.
	 * 
	 * @param item
	 */
	<T extends Item> void remove(T item) throws ModelNotFoundException;

	/**
	 * Removes the given item.
	 * 
	 * @param item
	 */
	@SuppressWarnings("unchecked")
	<T extends Item> void remove(T... items) throws ModelNotFoundException;

	/**
	 * Refreshes the given model's properties.
	 * 
	 * @param pk
	 * @return
	 * @throws ModelNotFoundException
	 */
	<T extends Item> void refresh(T item) throws ModelNotFoundException;

	/**
	 * Returns an object based on the given search parameters (key = property name,
	 * value = property value).
	 * 
	 * @param type
	 * @param searchParameters
	 *            if empty or null, all items of the given type will be returned.
	 * @return
	 */
	<T extends Item> List<T> getAll(Class<T> type, Map<String, Comparable<?>> searchParameters);

	/**
	 * Returns an object based on the given search parameters (key = property name,
	 * value = property value).
	 * 
	 * @param type
	 * @param searchParameters
	 *            if empty or null, all items of the given type will be returned.
	 * @param start
	 *            defines the amount of items that are being skipped.
	 * @param amount
	 *            starting from the start param this is the amount of items that
	 *            will be returned.
	 * @param loadAsProxy
	 *            the items will be just proxies that are lazy-loaded.
	 * @return
	 */
	<T extends Item> List<T> getAll(Class<T> type, Map<String, Comparable<?>> searchParameters, int page, int pageSize,
			boolean loadAsProxy);

	/**
	 * Returns all objects of the given type
	 * 
	 * @param pk
	 * @return
	 */
	<T extends Item> List<T> getAll(Class<T> type);

	/**
	 * If the given model is a proxy item (=only the pk is filled) then all of it's
	 * properties are filled.
	 * 
	 * @param item
	 */
	<T extends Item> void loadProxyModel(T proxyItem) throws ModelNotFoundException;

	/**
	 * Returns the item's value of the given property.
	 * 
	 * @param item
	 * @param propertyName
	 * @return
	 */
	<T extends Item> Object getPropertyValue(T item, String propertyName);

	/**
	 * Returns the item's value of the given property.
	 * 
	 * @param item
	 * @param propertyName
	 * @param valueType
	 * @return
	 */
	<T extends Item, V> V getPropertyValue(T item, String propertyName, Class<V> valueType);

	/**
	 * Sets the {@link Item}'s property to the given value.
	 * 
	 * @param item
	 * @param propertyName
	 * @param propertyValue
	 */
	<T extends Item> void setPropertyValue(T item, String propertyName, Object propertyValue);

	/**
	 * Detaches the given item models from the persistence context.
	 * 
	 * @see {@link PersistenceService#detach(Item...)}
	 * 
	 * @param items
	 */
	<T extends Item> void detach(T... items);

	/**
	 * @see {@link ModelService#detach(Item...)}
	 * 
	 * @param items
	 */
	<T extends Item> void detach(List<T> items);
}

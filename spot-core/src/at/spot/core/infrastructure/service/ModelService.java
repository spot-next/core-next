package at.spot.core.infrastructure.service;

import java.util.List;
import java.util.Map;

import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.type.PK;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.ModelNotUniqueException;

public interface ModelService {

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
	<T extends Item> void save(T model) throws ModelSaveException, ModelNotUniqueException;

	/**
	 * Saves all the given Item models. Referenced Item models will not be
	 * saved.
	 * 
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	<T extends Item> void saveAll(T... items) throws ModelSaveException, ModelNotUniqueException;

	/**
	 * Saves all the given Item models. Referenced Item models will not be
	 * saved.
	 * 
	 * @param type
	 * @return
	 */
	<T extends Item> void saveAll(List<T> models) throws ModelSaveException, ModelNotUniqueException;

	/**
	 * Returns an {@link Item} based on its PK.
	 * 
	 * @param pk
	 * @return
	 */
	<T extends Item> T get(Class<T> type, long pk) throws ModelNotFoundException;

	/**
	 * Returns an {@link Item} based on its {@link PK}.
	 * 
	 * @param pk
	 * @return
	 */
	<T extends Item> T get(PK pk) throws ModelNotFoundException;

	/**
	 * Returns the first {@link Item} based on the given search parameters (key
	 * = property name, value = property value).
	 * 
	 * @param pk
	 * @return
	 */
	<T extends Item> T get(Class<T> type, Map<String, Comparable<?>> searchParameters) throws ModelNotFoundException;

	/**
	 * Removes the given item.
	 * 
	 * @param item
	 */
	void remove(PK pk);

	/**
	 * Removes the given item.
	 * 
	 * @param item
	 */
	<T extends Item> void remove(Class<T> type, long pk);

	/**
	 * Removes the given item.
	 * 
	 * @param item
	 */
	<T extends Item> void remove(T item);

	/**
	 * Removes the given item.
	 * 
	 * @param item
	 */
	@SuppressWarnings("unchecked")
	<T extends Item> void remove(T... items);

	/**
	 * Refreshes the given model's properties.
	 * 
	 * @param pk
	 * @return
	 */
	<T extends Item> void refresh(T item) throws ModelNotFoundException;

	/**
	 * Returns an object based on the given search parameters (key = property
	 * name, value = property value).
	 * 
	 * @param pk
	 * @return
	 */
	<T extends Item> List<T> getAll(Class<T> type, Map<String, Comparable<?>> searchParameters)
			throws ModelNotFoundException;

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
	 * @return
	 */
	<T extends Item, V> V getPropertyValue(T item, String propertyName, Class<V> type);

	/**
	 * If the given model is a proxy item (=only the pk is filled) then all of
	 * it's properties are filled.
	 * 
	 * @param item
	 */
	<T extends Item> void loadProxyModel(T proxyItem) throws ModelNotFoundException;
}

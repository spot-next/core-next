package io.spotnext.core.infrastructure.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.spotnext.core.infrastructure.exception.ModelCreationException;
import io.spotnext.core.infrastructure.exception.ModelNotFoundException;
import io.spotnext.core.infrastructure.exception.ModelSaveException;
import io.spotnext.core.infrastructure.exception.ModelValidationException;
import io.spotnext.core.persistence.exception.ModelNotUniqueException;
import io.spotnext.core.persistence.query.ModelQuery;
import io.spotnext.core.persistence.service.PersistenceService;
import io.spotnext.infrastructure.type.Item;

/**
 * <p>
 * ModelService interface.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface ModelService {
	/**
	 * Creates an unsaved instance of the given type.
	 *
	 * @param      <T> the item type
	 * @param type a {@link java.lang.Class} object.
	 * @return a T object.
	 * @throws ModelCreationException if any.
	 */
	<T extends Item> T create(Class<T> type) throws ModelCreationException;

	/**
	 * Saves the given model. Referenced Item models will not be saved.
	 *
	 * @param       <T> the item type
	 * @param model a T object.
	 * @throws ModelSaveException       if any.
	 * @throws ModelNotUniqueException  if any.
	 * @throws ModelValidationException if any.
	 */
	<T extends Item> void save(T model) throws ModelSaveException, ModelNotUniqueException, ModelValidationException;

	/**
	 * Saves all the given Item models. Referenced Item models will not be saved.
	 *
	 * @param       <T> the item type
	 * @param items a T object.
	 * @throws ModelSaveException       if any.
	 * @throws ModelNotUniqueException  if any.
	 * @throws ModelValidationException if any.
	 */
	@SuppressWarnings("unchecked")
	<T extends Item> void saveAll(T... items)
			throws ModelSaveException, ModelNotUniqueException, ModelValidationException;

	/**
	 * Saves all the given Item models. Referenced Item models will not be saved.
	 *
	 * @param        <T> the item type
	 * @param models a {@link java.util.List} object.
	 * @throws ModelSaveException       if any.
	 * @throws ModelNotUniqueException  if any.
	 * @throws ModelValidationException if any.
	 */
	<T extends Item> void saveAll(List<T> models)
			throws ModelSaveException, ModelNotUniqueException, ModelValidationException;

	/**
	 * Returns an {@link io.spotnext.infrastructure.type.Item} based on its PK.
	 *
	 * @param      <T> the item type
	 * @param type a {@link java.lang.Class} object.
	 * @param pk   a long.
	 * @return a T object.
	 * @throws ModelNotFoundException if any.
	 */
	<T extends Item> T get(Class<T> type, long pk) throws ModelNotFoundException;

	/**
	 * Returns the first {@link io.spotnext.infrastructure.type.Item} based on the given
	 * search parameters (key = property name, value = property value).
	 *
	 * @param                  <T> the item type
	 * @param type             a {@link java.lang.Class} object.
	 * @param searchParameters a {@link java.util.Map} object.
	 * @param                  <T> a T object.
	 * @return a T object.
	 * @throws ModelNotUniqueException if there is more than one matching item.
	 */
	<T extends Item> T get(Class<T> type, Map<String, Object> searchParameters) throws ModelNotUniqueException;

	/**
	 * Returns the first {@link io.spotnext.infrastructure.type.Item} based on the given
	 * model query parameters. the
	 * {@link io.spotnext.core.persistence.query.ModelQuery} allows you to set the
	 * fetching strategy, pagination and to limit the result.
	 *
	 * @param       <T> the item type
	 * @param query a {@link io.spotnext.core.persistence.query.ModelQuery} object.
	 * @param       <T> a T object.
	 * @return a T object.
	 * @throws ModelNotUniqueException if there is more than one matching item.
	 */
	<T extends Item> T get(ModelQuery<T> query) throws ModelNotUniqueException;;

	/**
	 * Returns an object based on the given search parameters (key = property name,
	 * value = property value).
	 *
	 * @param                  <T> the item type
	 * @param searchParameters if empty or null, all items of the given type will be
	 *                         returned.
	 * @param type             a {@link java.lang.Class} object.
	 * @param                  <T> a T object.
	 * @return a {@link java.util.List} object.
	 */
	<T extends Item> List<T> getAll(Class<T> type, Map<String, Object> searchParameters);

	/**
	 * Returns an object based on the given search parameters (key = property name,
	 * value = property value).
	 *
	 * @param       <T> the item type
	 * @param query a {@link io.spotnext.core.persistence.query.ModelQuery} object.
	 * @return a {@link java.util.List} object.
	 */
	<T extends Item> List<T> getAll(ModelQuery<T> query);

	/**
	 * Returns the first {@link io.spotnext.infrastructure.type.Item} based on the given
	 * example item.
	 *
	 * @param         <T> the item type
	 * @param         <T> the item type
	 * @param example the example item instance.
	 * @param         <T> a T object.
	 * @return a T object.
	 */
	<T extends Item> T getByExample(T example);

	/**
	 * Returns all {@link io.spotnext.infrastructure.type.Item}s that match the given example
	 * item.
	 *
	 * @param         <T> the item type
	 * @param example the example item instance.
	 * @param         <T> a T object.
	 * @return a {@link java.util.List} object.
	 */
	<T extends Item> List<T> getAllByExample(T example);

	/**
	 * Removes the given item.
	 *
	 * @param      <T> the item type
	 * @param type a {@link java.lang.Class} object.
	 * @param pk   a long.
	 * @throws ModelNotFoundException if any.
	 */
	<T extends Item> void remove(Class<T> type, long pk) throws ModelNotFoundException;

	/**
	 * Removes the given items.
	 *
	 * @param       <T> the item type
	 * @param items a {@link java.util.List} object.
	 * @throws ModelNotFoundException if any.
	 */
	<T extends Item> void removeAll(List<T> items) throws ModelNotFoundException;

	/**
	 * Removes the given item.
	 *
	 * @param       <T> the item type
	 * @param items a T object.
	 * @throws ModelNotFoundException if any.
	 */
	@SuppressWarnings("unchecked")
	<T extends Item> void remove(T... items) throws ModelNotFoundException;

	/**
	 * Refreshes the given model's properties.
	 *
	 * @param      <T> the item type
	 * @param item a T object.
	 * @throws ModelNotFoundException if any.
	 */
	<T extends Item> void refresh(T item) throws ModelNotFoundException;

	/**
	 * Returns the item's value of the given property.
	 *
	 * @param              <T> the item type
	 * @param item         a T object.
	 * @param propertyName a {@link java.lang.String} object.
	 * @return a {@link java.lang.Object} object.
	 */
	<T extends Item> Object getPropertyValue(T item, String propertyName);

	/**
	 * Returns the item's value of the given property.
	 *
	 * @param              <T> the item type
	 * @param item         a T object.
	 * @param propertyName a {@link java.lang.String} object.
	 * @param valueType    a {@link java.lang.Class} object.
	 * @param              <T> a T object.
	 * @param              <V> a V object.
	 * @return a V object.
	 */
	<T extends Item, V> V getPropertyValue(T item, String propertyName, Class<V> valueType);

	/**
	 * Returns the item's value of the given property.
	 *
	 * @param              <T> the item type
	 * @param item         a T object.
	 * @param propertyName a {@link java.lang.String} object.
	 * @param valueType    a {@link java.lang.Class} object.
	 * @param locale       a {@link java.util.Locale} object.
	 * @param              <T> a T object.
	 * @param              <V> a V object.
	 * @return a V object.
	 */
	<T extends Item, V> V getLocalizedPropertyValue(T item, String propertyName, Class<V> valueType, Locale locale);

	/**
	 * Sets the {@link io.spotnext.infrastructure.type.Item}'s property to the given value.
	 *
	 * @param               <T> the item type
	 * @param item          a T object.
	 * @param propertyName  a {@link java.lang.String} object.
	 * @param propertyValue a {@link java.lang.Object} object.
	 * @param               <T> a T object.
	 */
	<T extends Item> void setPropertyValue(T item, String propertyName, Object propertyValue);

	/**
	 * Sets the {@link io.spotnext.infrastructure.type.Item}'s property to the given value.
	 *
	 * @param               <T> the item type
	 * @param item          a T object.
	 * @param propertyName  a {@link java.lang.String} object.
	 * @param propertyValue a {@link java.lang.Object} object.
	 * @param locale        a {@link java.util.Locale} object.
	 * @param               <T> a T object.
	 */
	<T extends Item> void setLocalizedPropertyValue(T item, String propertyName, Object propertyValue, Locale locale);

	/**
	 * Detaches the given item models from the persistence context.
	 *
	 * @param <T> the item type
	 * @see PersistenceService#detach(List)
	 * @param items a T object.
	 * @param       <T> a T object.
	 */
	<T extends Item> void detach(T... items);

	/**
	 * <p>
	 * detach.
	 * </p>
	 *
	 * @param <T> the item type
	 * @see ModelService#detach(Item...)
	 * @param items a {@link java.util.List} object.
	 * @param       <T> a T object.
	 */
	<T extends Item> void detach(List<T> items);

	/**
	 * Checks if the given item is attached to the persistence context.
	 *
	 * @param      <T> the item type
	 * @param item the item to check
	 * @return true if the item is attached
	 * @param <T> a T object.
	 */
	<T extends Item> boolean isAttached(T item);

	/**
	 * Attaches the given item to the persistence context.
	 *
	 * @param      <T> the item type
	 * @param item to attach
	 * @throws ModelNotFoundException
	 * @param <T> a T object.
	 */
	<T extends Item> void attach(T item) throws ModelNotFoundException;
}

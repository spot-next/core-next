package at.spot.core.persistence.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import at.spot.core.persistence.query.Query;

import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.exception.ModelValidationException;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.exception.QueryException;

@Service
public interface PersistenceService {

	static final List<Class<?>> NATIVE_DATATYPES = java.util.Arrays.asList(Boolean.class, String.class, Integer.class,
			Long.class, Double.class, Float.class, Byte.class, Short.class, BigDecimal.class, BigInteger.class,
			Character.class, Date.class, java.sql.Date.class, Time.class, Timestamp.class, Calendar.class);

	/**
	 * Saves the given models and all of its dependent models.
	 * 
	 * @param type
	 * @return
	 */
	<T extends Item> void save(List<T> models) throws ModelSaveException, ModelNotUniqueException;

	/**
	 * Returns an object based on its PK.
	 * 
	 * @param pk
	 * @return
	 */
	<T extends Item> T load(Class<T> type, long pk) throws ModelNotFoundException;

	/**
	 * Refreshes the given model's properties.
	 * 
	 * @param pk
	 * @return
	 * @throws ModelNotFoundException
	 */
	<T extends Item> void refresh(List<T> item) throws ModelNotFoundException;

	/**
	 * Returns an object based on the given search parameters (key = property name,
	 * value = property value).
	 * 
	 * @param type
	 * @param searchParameters
	 *            if empty or null, all items of the given type will be returned.
	 */
	<T extends Item> List<T> load(Class<T> type, Map<String, Object> searchParameters);

	/**
	 * Returns the paginated results for the given query.
	 */
	<T> List<T> query(Query<T> query) throws QueryException;

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
	 *            will be returned. the items will be just proxies that are
	 *            lazy-loaded.
	 */
	<T extends Item> List<T> load(final Class<T> type, final Map<String, Object> searchParameters, final Integer page,
			final Integer pageSize);

	/**
	 * Removes the given item.
	 */
	<T extends Item> void remove(List<T> items);

	/**
	 * Removes the item of the given type with the given PK.
	 */
	<T extends Item> void remove(Class<T> type, long pk);

	/**
	 * Saves the database to disk. This has to be done before the application quits
	 * to prevent data corruption.
	 */
	void saveDataStorage();

	/**
	 * Clears all stored data from the storage.
	 */
	void clearDataStorage();

	/**
	 * Initialize a new item.
	 * 
	 * @param joinPoint
	 * @param rel
	 * @return
	 * @throws Throwable
	 */
	<T extends Item> void initItem(T item);

	/**
	 * Detaches a given item model from the underlying persistence implementation.
	 * This is useful if serializing the item causes problems. The effect can be
	 * different depending on the persistence service implementation, but in general
	 * lazy-loading properties will not work anymore afterwards.
	 * 
	 * @param items
	 */
	<T extends Item> void detach(List<T> items);

	/**
	 * Converts the given item to a map.
	 * 
	 * @param item
	 * @return
	 * @throws ModelValidationException
	 */
	<T extends Item> Map<String, Object> convertItemToMap(T item);
}

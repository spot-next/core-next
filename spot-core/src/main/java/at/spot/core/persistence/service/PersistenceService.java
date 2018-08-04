package at.spot.core.persistence.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.exception.QueryException;
import at.spot.core.persistence.query.JpqlQuery;
import at.spot.core.persistence.query.ModelQuery;
import at.spot.core.types.Item;

@Service
public interface PersistenceService {

	static final List<Class<?>> NATIVE_DATATYPES = Collections
			.unmodifiableList(java.util.Arrays.asList(Boolean.class, String.class, Integer.class, Long.class,
					Double.class, Float.class, Byte.class, Short.class, BigDecimal.class, BigInteger.class,
					Character.class, Date.class, java.sql.Date.class, Time.class, Timestamp.class, Calendar.class));

	/**
	 * Saves the given models and all of its dependent models.
	 */
	<T extends Item> void save(List<T> models) throws ModelSaveException, ModelNotUniqueException;

	/**
	 * Refreshes the given model's properties.
	 * 
	 * @throws ModelNotFoundException
	 */
	<T extends Item> void refresh(List<T> item) throws ModelNotFoundException;

	/**
	 * Returns the paginated results for the given query.
	 */
	<T> List<T> query(JpqlQuery<T> query) throws QueryException;

	/**
	 * Returns an object based on its PK.
	 */
	<T extends Item> T load(Class<T> type, long pk) throws ModelNotFoundException;

	/**
	 * Returns an list of items based on the given search query.
	 */
	<T extends Item> List<T> load(ModelQuery<T> query);

	/**
	 * Removes the given item.
	 */
	<T extends Item> void remove(List<T> items);

	/**
	 * Removes the item of the given type with the given PK.
	 */
	<T extends Item> void remove(Class<T> type, long pk);

	/**
	 * Saves the database to disk. This has to be done before the application
	 * quits to prevent data corruption.
	 */
	void saveDataStorage();

	/**
	 * Clears all stored data from the storage.
	 */
	void clearDataStorage();

	/**
	 * Initialize a new item.
	 */
	<T extends Item> void initItem(T item);

	/**
	 * Detaches a given item model from the underlying persistence
	 * implementation. This is useful if serializing the item causes problems.
	 * The effect can be different depending on the persistence service
	 * implementation, but in general lazy-loading properties will not work
	 * anymore afterwards.
	 */
	<T extends Item> void detach(List<T> items);

	/**
	 * Converts the given item to a map.
	 */
	<T extends Item> Map<String, Object> convertItemToMap(T item);
}

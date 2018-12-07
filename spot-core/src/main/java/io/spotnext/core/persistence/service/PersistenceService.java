package io.spotnext.core.persistence.service;

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

import io.spotnext.core.infrastructure.exception.ModelNotFoundException;
import io.spotnext.core.infrastructure.exception.ModelSaveException;
import io.spotnext.core.persistence.exception.ModelNotUniqueException;
import io.spotnext.core.persistence.exception.QueryException;
import io.spotnext.core.persistence.query.JpqlQuery;
import io.spotnext.core.persistence.query.ModelQuery;
import io.spotnext.core.persistence.query.QueryResult;
import io.spotnext.infrastructure.type.Item;

/**
 * <p>PersistenceService interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public interface PersistenceService {

	/** Constant <code>NATIVE_DATATYPES</code> */
	static final List<Class<?>> NATIVE_DATATYPES = Collections
			.unmodifiableList(java.util.Arrays.asList(Boolean.class, String.class, Integer.class, Long.class,
					Double.class, Float.class, Byte.class, Short.class, BigDecimal.class, BigInteger.class,
					Character.class, Date.class, java.sql.Date.class, Time.class, Timestamp.class, Calendar.class));

	/**
	 * Saves the given models and all of its dependent models.
	 *
	 * @param models a {@link java.util.List} object.
	 * @throws io.spotnext.infrastructure.exception.ModelSaveException if any.
	 * @throws io.spotnext.core.persistence.exception.ModelNotUniqueException if any.
	 */
	<T extends Item> void save(List<T> models) throws ModelSaveException, ModelNotUniqueException;

	/**
	 * Refreshes the given model's properties.
	 *
	 * @throws io.spotnext.infrastructure.exception.ModelNotFoundException
	 * @param item a {@link java.util.List} object.
	 * @param      <T> a T object.
	 */
	<T extends Item> void refresh(List<T> item) throws ModelNotFoundException;

	/**
	 * Returns the paginated results for the given query.
	 *
	 * @param query a {@link io.spotnext.core.persistence.query.JpqlQuery} object.
	 * @return the query result, never null
	 * @throws io.spotnext.core.persistence.exception.QueryException if any.
	 */
	<T> QueryResult<T> query(JpqlQuery<T> query) throws QueryException;

	/**
	 * Returns an entity with the given.
	 *
	 * @param type        a {@link java.lang.Class} object.
	 * @param id          a long.
	 * @param returnProxy specifies if the returned item should be a proxy (hence no database access is performed) or fetched from the database
	 * @return a T object.
	 * @throws io.spotnext.infrastructure.exception.ModelNotFoundException if any.
	 */
	<T extends Item> T load(Class<T> type, long id, boolean returnProxy) throws ModelNotFoundException;

	/**
	 * Returns an list of items based on the given search query.
	 *
	 * @param query a {@link io.spotnext.core.persistence.query.ModelQuery} object.
	 * @return a {@link java.util.List} object.
	 */
	<T extends Item> List<T> load(ModelQuery<T> query);

	/**
	 * Removes the given item.
	 *
	 * @param items a {@link java.util.List} object.
	 */
	<T extends Item> void remove(List<T> items);

	/**
	 * Removes the item of the given type with the given ID.
	 *
	 * @param type a {@link java.lang.Class} object.
	 * @param id   a long.
	 */
	<T extends Item> void remove(Class<T> type, long id);

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
	 * @param item a T object.
	 */
	<T extends Item> void initItem(T item);

	/**
	 * Detaches a given item model from the underlying persistence implementation.
	 * This is useful if serializing the item causes problems. The effect can be
	 * different depending on the persistence service implementation, but in general
	 * lazy-loading properties will not work anymore afterwards.
	 *
	 * @param items a {@link java.util.List} object.
	 */
	<T extends Item> void detach(List<T> items);

	/**
	 * Converts the given item to a map.
	 *
	 * @param item a T object.
	 * @return a {@link java.util.Map} object.
	 */
	<T extends Item> Map<String, Object> convertItemToMap(T item);

	/**
	 * Checks if the given item is attached to the persistence context.
	 *
	 * @param item the item to check
	 * @return true if the item is attached
	 * @param <T> a T object.
	 */
	<T extends Item> boolean isAttached(T item);

	/**
	 * Attaches the given item in case it is detached.
	 *
	 * @param item a T object.
	 * @return true if the item was successfully attached to the persistence
	 *         context.
	 * @throws io.spotnext.infrastructure.exception.ModelNotFoundException
	 * @param <T> a T object.
	 */
	<T extends Item> boolean attach(T item) throws ModelNotFoundException;

	/**
	 * Unbinds the current session. This is useful to free up a session if an
	 * uncaught exception is thrown in a thread.
	 */
	void unbindSession();

	/**
	 * Clears all internal persistence-related caches.
	 */
	void evictCaches();
}

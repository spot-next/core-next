package at.spot.core.persistence.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import at.spot.core.data.model.Item;
import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.persistence.exception.CannotCreateModelProxyException;

@Service
public interface PersistenceService {
	/**
	 * Saves the given model and all of its dependent models.
	 * 
	 * @param type
	 * @return
	 */
	<T extends Item> void save(T model) throws ModelSaveException;

	/**
	 * Saves the given models and all of its dependent models.
	 * 
	 * @param type
	 * @return
	 */
	<T extends Item> void saveAll(T... models) throws ModelSaveException;

	/**
	 * Saves the given models and all of its dependent models.
	 * 
	 * @param type
	 * @return
	 */
	<T extends Item> void saveAll(List<T> models) throws ModelSaveException;

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
	 */
	<T extends Item> void refresh(T item) throws ModelNotFoundException;

	/**
	 * Returns an object based on the given search parameters (key = property
	 * name, value = property value).
	 * 
	 * @param pk
	 * @return
	 */
	<T extends Item> List<T> load(Class<T> type, Map<String, Object> searchParameters) throws ModelNotFoundException;

	/**
	 * Fills the given proxy item with it's property values.
	 * 
	 * @param pk
	 * @return
	 */
	<T extends Item> void loadProxyModel(T proxyItem) throws ModelNotFoundException;

	/**
	 * Creates a new proxy item that references the given item.
	 * 
	 * @param pk
	 * @return
	 */
	<T extends Item> T createProxyModel(T item) throws CannotCreateModelProxyException;

	/**
	 * Initiates the datastorage based on the registeredTypes.
	 */
	void initDataStorage();

	/**
	 * Saves the database to disk. This has to be done before the application
	 * quits to prevent data corruption.
	 */
	void saveDataStorage();
}

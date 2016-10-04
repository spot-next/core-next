package at.spot.core.persistence.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import at.spot.core.data.model.Item;
import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;

@Service
public interface PersistenceService<T extends Item> {
	/**
	 * Saved the given model and all of its dependent models.
	 * 
	 * @param type
	 * @return
	 */
	void save(T model) throws ModelSaveException;

	/**
	 * Returns an object based on its PK.
	 * 
	 * @param pk
	 * @return
	 */
	T load(Class<T> type, Long pk) throws ModelNotFoundException;

	/**
	 * Returns an object based on the given search parameters (key = property
	 * name, value = property value).
	 * 
	 * @param pk
	 * @return
	 */
	List<T> load(Class<T> type, Map<String, Object> searchParameters) throws ModelNotFoundException;

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

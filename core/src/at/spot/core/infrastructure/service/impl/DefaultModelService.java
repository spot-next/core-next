package at.spot.core.infrastructure.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.model.Item;
import at.spot.core.persistence.service.PersistenceService;

@Service
public class DefaultModelService extends AbstractModelService {

	@Autowired
	protected PersistenceService persistenceService;

	Map<Long, Item> models = new HashMap<>();

	@Override
	public <T extends Item> void save(T model) throws ModelSaveException {
		persistenceService.save(model);
	}

	@Override
	public <T extends Item> T get(Class<T> type, Long pk) throws ModelNotFoundException {
		return (T) persistenceService.load(type, pk);
	}

	@Override
	public <T extends Item> List<T> get(Class<T> type, Map<String, Object> searchParameters)
			throws ModelNotFoundException {

		return persistenceService.load(type, searchParameters);
	}
}

package at.spot.core.persistence.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;

import at.spot.core.data.model.Item;
import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.type.ItemTypeDefinition;
import at.spot.core.infrastructure.type.ItemTypePropertyDefinition;
import at.spot.core.persistence.service.PersistenceService;

@Service
public class CqEngineDBService implements PersistenceService {

	Map<String, IndexedCollection<? extends Item>> storage;

	@Autowired
	TypeService typeService;

	@Log(message = "Initializing CQEngine storage ...")
	@Override
	public void initDataStorage() {
		for (ItemTypeDefinition t : typeService.getItemTypeDefinitions().values()) {
			IndexedCollection<? extends Item> itemStorage = new ConcurrentIndexedCollection<>();

			for (ItemTypePropertyDefinition p : typeService.getItemTypeProperties(t.typeCode).values()) {
				if (p.isUnique) {
					// itemStorage.addIndex(NavigableIndex.onAttribute(t.typeName));
				}
			}

			storage.put(t.typeClass, itemStorage);
		}
	}

	@Override
	public <T extends Item> void save(T model) throws ModelSaveException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends Item> void saveAll(T... models) throws ModelSaveException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends Item> void saveAll(List<T> models) throws ModelSaveException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends Item> T load(Class<T> type, long pk) throws ModelNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Item> void refresh(T item) throws ModelNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends Item> List<T> load(Class<T> type, Map<String, Object> searchParameters)
			throws ModelNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Item> void loadProxyModel(T item) throws ModelNotFoundException {
		refresh(item);
	}

	@Override
	public <T extends Item> T createProxyModel(T item) {
		return null;
	}

	@Override
	public void saveDataStorage() {
		// TODO Auto-generated method stub

	}

}

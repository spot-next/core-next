package at.spot.core.persistence.service.impl;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.mapdb.serializer.SerializerArrayTuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.model.Item;
import at.spot.core.persistence.service.PersistenceService;

@Service
public class MapDBService implements PersistenceService {
	private DB database;
	private Map<String, BTreeMap<Object[], Integer>> dataStorage = new HashMap<>();

	@Autowired
	ModelService modelService;

	@Override
	public void initDataStorage() {
		database = DBMaker.fileDB("storage/database.db").make();

		List<Class<? extends Item>> itemTypes = modelService.getAvailableTypes();

		for (Class<? extends Item> t : itemTypes) {
			BTreeMap<Object[], Integer> map = database.treeMap("towns")
					.keySerializer(new SerializerArrayTuple(Serializer.LONG, Serializer.STRING, Serializer.STRING))
					.valueSerializer(Serializer.JAVA).createOrOpen();

			dataStorage.put(t.getSimpleName(), map);
		}
	}

	protected <T extends Item> BTreeMap<Object[], Integer> getDataStorageForType(Class<T> type) {
		return this.dataStorage.get(type.getSimpleName());
	}

	@Override
	public <T extends Item> void save(T model) throws ModelSaveException {
		BTreeMap<Object[], Integer> storage = getDataStorageForType(model.getClass());

		model.pk = getNextPk();

		// storage.put(model.pk, model);
	}

	protected long getNextPk() {
		Set<Long> pks = new HashSet<>();

		long newPK = 0l;

		for (BTreeMap<Object[], Integer> s : dataStorage.values()) {
			pks.addAll(s.keySet());
		}

		if (pks.size() > 0) {
			newPK = pks.stream().max(Long::compare).get() + 1;
		}

		return newPK;
	}

	protected Map<String, Item> decomposeItem(Item item) {
		Map<Long, Item> storage = getDataStorageForType(model.getClass());

		if (item.pk != null) {

		}

		Map<String, Object> objectMap = new HashMap<>();

		BeanInfo beanInfo = Introspector.getBeanInfo(item.getClass());

		for (PropertyDescriptor propertyDesc : beanInfo.getPropertyDescriptors()) {
			String propertyName = propertyDesc.getName();
			Object propertyValue = propertyDesc.getValue(propertyName);

			// we found a property on the item model that is also an item model
			// -> we have to replace it with a reference object, and save the
			// original object in it's own datastorage.
			if (propertyDesc.getPropertyType().isInstance(Item.class) && propertyValue != null) {

			}
		}

		return items;
	}

	@Override
	public <T extends Item> T load(Class<T> type, Long pk) throws ModelNotFoundException {
		T item = (T) getDataStorageForType(type).get(pk);

		return item;
	}

	@Override
	public <T extends Item> List<T> load(Class<T> type, Map<String, Object> searchParameters)
			throws ModelNotFoundException {
		List<T> ret = new ArrayList<>();

		// try {
		// for (Item i : models.values()) {
		// boolean found = true;
		//
		// for (String property : searchParameters.keySet()) {
		// Object searchValue = searchParameters.get(property);
		//
		// Object value = i.getProperty(property);
		//
		// if (ObjectUtils.notEqual(searchValue, value)) {
		// found = false;
		// break;
		// }
		// }
		//
		// if (found) {
		// ret.add((T) i);
		// }
		// }
		// } catch (PropertyNotAccessibleException e) {
		// throw new ModelNotFoundException();
		// }

		return ret;
	}

	@Override
	public void saveDataStorage() {
		database.close();
	}

}

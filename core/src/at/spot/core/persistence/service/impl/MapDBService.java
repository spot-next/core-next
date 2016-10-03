package at.spot.core.persistence.service.impl;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.mapdb.serializer.SerializerArrayTuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.model.Item;
import at.spot.core.persistence.service.PersistenceService;

@Service
public class MapDBService implements PersistenceService<Item> {
	private DB database;
	private Map<String, BTreeMap<Object[], Integer>> dataStorage = new HashMap<>();

	@Autowired
	ModelService modelService;

	@Autowired
	TypeService typeService;

	@Override
	public void initDataStorage() {
		database = DBMaker.fileDB("storage/database.db").make();

		List<Class<? extends Item>> itemTypes = typeService.getAvailableTypes();

		for (Class<? extends Item> t : itemTypes) {
			BTreeMap<Object[], Integer> map = database.treeMap("towns")
					.keySerializer(new SerializerArrayTuple(Serializer.LONG, Serializer.STRING, Serializer.JAVA))
					.valueSerializer(Serializer.JAVA).createOrOpen();

			dataStorage.put(t.getSimpleName(), map);
		}
	}

	protected BTreeMap<Object[], Integer> getDataStorageForType(Class<? extends Item> type) {
		return this.dataStorage.get(type.getSimpleName());
	}

	@Override
	public void save(Item model) throws ModelSaveException {
		try {
			saveInternal(model);
		} catch (IntrospectionException e) {
			throw new ModelSaveException(e);
		}
	}

	protected long getNextPk(Item model) {
		Set<Long> pks = new HashSet<>();

		long newPK = 0l;

		for (BTreeMap<Object[], Integer> s : dataStorage.values()) {
			for (Object[] o : s.getKeys()) {
				pks.add((Long) o[0]);
			}
		}

		if (pks.size() > 0) {
			newPK = pks.stream().max(Long::compare).get() + 1;
		}

		return newPK;
	}

	protected void saveInternal(Item item) throws IntrospectionException {
		BTreeMap<Object[], Integer> storage = getDataStorageForType(item.getClass());
		Long newPk = getNextPk(item);

		if (item.pk == null) {
			item.pk = getNextPk(item);
		}

		try {
			ObjectMapper m = new ObjectMapper();
			Map<String, Object> props = m.convertValue(item.getClass(), Map.class);

			for (Field field : item.getClass().getFields()) {
				Object fieldValue = field.get(item);

				if (fieldValue != null && typeService.hasAnnotation(field, Property.class)) {
					// check for collections
					if (field.getGenericType() instanceof ParameterizedType) {
						ParameterizedType paramType = (ParameterizedType) field.getGenericType();

						if (paramType != null) {
							for (Type c : paramType.getActualTypeArguments()) {
								if (c.getClass().isInstance(Item.class)) {
									Collection<Item> col = (Collection<Item>) fieldValue;

									for (Item collectionItem : col) {
										saveInternal(collectionItem);
									}
								}
							}
						}
					}

					// we found a property on the item model that is also an
					// item
					// model
					// -> we have to replace it with a reference object, and
					// save
					// the
					// original object in it's own datastorage.
					if (field.getType().isInstance(Item.class)) {
						saveInternal((Item) fieldValue);
					}

					storage.put(new Object[] { item.pk, field.getName(), fieldValue }, 1);
				}
			}
		} catch (Exception e) {
			database.rollback();
		}

		database.commit();
	}

	@Override
	public Item load(Class<Item> type, Long pk) throws ModelNotFoundException {
		NavigableMap<Object[], Integer> items = getDataStorageForType(type).prefixSubMap(new Object[] { pk });

		Item found = null;
		try {
			found = type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return found;
	}

	@Override
	public List<Item> load(Class<Item> type, Map<String, Object> searchParameters) throws ModelNotFoundException {
		// List<T> ret = new ArrayList<>();

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

		return null;
	}

	@Override
	public void saveDataStorage() {
		database.close();
	}

}

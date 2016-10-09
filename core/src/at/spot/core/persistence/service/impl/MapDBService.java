package at.spot.core.persistence.service.impl;

import java.beans.IntrospectionException;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.mapdb.serializer.SerializerArrayTuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.data.model.Item;
import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.exception.PropertyNotAccessibleException;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.type.PK;
import at.spot.core.persistence.service.PersistenceService;

@Service
public class MapDBService implements PersistenceService {
	private DB database;
	private Map<String, BTreeMap<Object[], PK>> dataStorage = new HashMap<>();

	@Autowired
	protected ModelService modelService;

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected LoggingService loggingService;

	@Log(message = "Initializing MapDB storage ...")
	@Override
	public void initDataStorage() {
		try {
			database = DBMaker.fileDB("storage/database.db").transactionEnable().make();

			List<Class<? extends Item>> itemTypes = typeService.getAvailableTypes();

			for (Class<? extends Item> t : itemTypes) {
				BTreeMap<Object[], PK> map = database.treeMap(t.getClass().getSimpleName())
						.keySerializer(new SerializerArrayTuple(Serializer.LONG, Serializer.STRING, Serializer.JAVA))
						.valueSerializer(Serializer.JAVA).createOrOpen();

				dataStorage.put(t.getSimpleName(), map);
			}
		} catch (Exception e) {
			// org.mapdb.DBException$DataCorruption
			loggingService.error(e.getMessage());
		}
	}

	protected BTreeMap<Object[], PK> getDataStorageForType(Class<? extends Item> type) {
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

	protected PK getNextPk(Item item) {
		Set<Long> pks = new HashSet<>();

		long newPK = 0l;

		for (BTreeMap<Object[], PK> s : dataStorage.values()) {
			for (Object[] o : s.getKeys()) {
				pks.add((Long) o[0]);
			}
		}

		if (pks.size() > 0) {
			newPK = pks.stream().max(Long::compare).get() + 1;
		}

		return new PK(newPK, item.getClass());
	}

	protected void saveInternal(Item item) throws IntrospectionException {
		BTreeMap<Object[], PK> storage = getDataStorageForType(item.getClass());
		if (item.pk == null) {
			item.pk = getNextPk(item);
		}

		Map<String, Object> itemAttributes = new HashMap<>();

		try {
			Map<String, Member> itemMembers = typeService.getItemProperties(item.getClass());

			for (Member member : itemMembers.values()) {
				if (StringUtils.equalsIgnoreCase(member.getName(), "pk")) {
					continue;
				}

				Object value = modelService.getPropertyValue(item, member.getName());

				if (value != null) {
					// if this is a reference to another item model, we just
					// save the pk
					if (value instanceof Item) {
						Item subItem = (Item) value;
						if (!subItem.isPersisted()) {
							saveInternal(subItem);
						}

						itemAttributes.put(member.getName(), ((Item) value).pk);
					} else if (value.getClass().isArray()) {
						// Object[] objects = (Object[]) value;
						//
						// for (Object o : objects) {
						// if (o != null) {
						// if (o instanceof Item) {
						//
						// } else {
						// itemAttributes.put(key, value)
						// }
						// }
						// }
					} else if (Collection.class.isAssignableFrom(value.getClass())) {

					} else if (Map.class.isAssignableFrom(value.getClass())) {

					} else {
						itemAttributes.put(member.getName(), value);
					}
				}
			}

			for (String k : itemAttributes.keySet()) {
				storage.put(new Object[] { item.pk.longValue(), k, itemAttributes.get(k) }, item.pk);
			}
		} catch (Exception e) {
			database.rollback();
		}

		database.commit();
	}

	@Override
	public <T extends Item> T load(Class<T> type, long pk) throws ModelNotFoundException {
		NavigableMap<Object[], PK> items = getDataStorageForType(type).prefixSubMap(new Object[] { pk });

		T found = null;

		if (items.values().size() > 0) {
			try {
				found = type.newInstance();

				for (Object[] k : items.keySet()) {
					if (found.pk == null) {
						found.pk = items.get(k);
					}

					String propertyName = (String) k[1];
					Object propertyValue = k[2];

					found.setProperty(propertyName, propertyValue);
				}

			} catch (Exception | PropertyNotAccessibleException e) {
				throw new ModelNotFoundException(e);
			}
		}

		return found;
	}

	@Override
	public <T extends Item> List<T> load(Class<T> type, Map<String, Object> searchParameters) {
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
	public <T extends Item> T loadProxyModel(T proxyItem) throws ModelNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveDataStorage() {
		database.close();
	}

}

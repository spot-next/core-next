package at.spot.core.persistence.service.impl;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.mapdb.DB;
import org.mapdb.DBException;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.service.ConfigurationService;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.type.ItemTypeDefinition;
import at.spot.core.infrastructure.type.ItemTypePropertyDefinition;
import at.spot.core.infrastructure.type.LogLevel;
import at.spot.core.infrastructure.type.PK;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.CannotCreateModelProxyException;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.persistence.service.impl.mapdb.Entity;
import at.spot.core.support.util.ClassUtil;

@Service
public class MapDBService implements PersistenceService {

	protected static final String CONFIG_KEY_STORAGE_FILE = "service.persistence.mapdb.filepath";
	protected static final String DEFAULT_DB_FILEPATH = "/var/tmp/storage.db";

	protected static final String PK_PROPERTY_NAME = "pk";

	private static DB database;
	private Map<String, DataStorage> dataStorage = new HashMap<>();

	@Autowired
	protected ModelService modelService;

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected LoggingService loggingService;

	@Autowired
	protected ConfigurationService configurationService;

	@Log(message = "Initializing MapDB storage ...")
	@Override
	public void initDataStorage() {
		try {
			database = DBMaker.fileDB(configurationService.getString(CONFIG_KEY_STORAGE_FILE, DEFAULT_DB_FILEPATH))
					.fileMmapEnable().fileMmapPreclearDisable().cleanerHackEnable().transactionEnable()
					.allocateStartSize(50 * 1024 * 1024).allocateIncrement(50 * 1024 * 1024).make();

			Map<String, ItemTypeDefinition> itemTypes = typeService.getItemTypeDefinitions();

			// dataStorage =
			// database.hashMap("items").keySerializer(Serializer.LONG)
			// .valueSerializer(new ItemSerializer<>()).createOrOpen();

			for (ItemTypeDefinition t : itemTypes.values()) {
				dataStorage.put(t.typeClass,
						new DataStorage(t, typeService.getItemTypeProperties(t.typeCode).values()));
			}
		} catch (Exception e) {
			// org.mapdb.DBException$DataCorruption
			loggingService.error(e.getMessage());
		}

		loggingService.debug("MapDB service initialized");
	}

	protected DataStorage getDataStorageForType(Class<? extends Item> type) {
		return this.dataStorage.get(type.getName());
	}

	/**
	 * Try to laod an item with the same unique properties. If there already is
	 * one stored, the given item is not unique.
	 * 
	 * @param model
	 * @return
	 */
	protected boolean isUnique(Item model) {
		boolean isUnique = true;

		DataStorage storage = getDataStorageForType(model.getClass());

		if (!model.isPersisted() && storage.get(model.getUniqueProperties()).size() > 0) {
			isUnique = false;
		}

		return isUnique;
	}

	@Override
	public void save(Item model) throws ModelSaveException, ModelNotUniqueException {
		try {
			saveInternal(model, true);
		} catch (IntrospectionException e) {
			throw new ModelSaveException(e);
		}
	}

	@Override
	public <T extends Item> void saveAll(T... models) throws ModelSaveException, ModelNotUniqueException {
		saveAll(Arrays.asList(models));
	}

	@Override
	public <T extends Item> void saveAll(List<T> models) throws ModelSaveException, ModelNotUniqueException {
		long start = System.currentTimeMillis();
		long duration = start;

		try {
			long i = 0;
			int saveAfter = 100;

			for (Item model : models) {
				saveInternal(model, false);

				if (i > 0 && i % saveAfter == 0) {
					// database.commit();

					duration = System.currentTimeMillis() - start;
					if (duration >= 1000) {
						loggingService.debug("Created " + i + " users (" + i / (duration / 1000) + " items/s )");
					}
				}

				i++;
			}

			database.commit();
		} catch (IntrospectionException e) {
			throw new ModelSaveException(e);
		}
	}

	// @Log(logLevel = LogLevel.DEBUG, measureTime = true, after = true)
	protected void saveInternal(Item item, boolean commit) throws IntrospectionException, ModelNotUniqueException {

		// if there is nothing to save, we return immediately
		if (item.isPersisted() && !item.isDirty()) {
			return;
		}

		// check if there is already an item with the same unique properties
		// if (!isUnique(item)) {
		// throw new ModelNotUniqueException(
		// "Cannot save model because there is already a model with the same
		// uniqueness criteria");
		// }

		// now iterate over all attributes and check for item references and
		// also save them
		try {
			Map<String, ItemTypePropertyDefinition> itemMembers = typeService.getItemTypeProperties(item.getClass());

			Entity entity = new Entity();

			for (ItemTypePropertyDefinition member : itemMembers.values()) {
				Object value = modelService.getPropertyValue(item, member.name);

				// ignore pk property
				if (value != null && !StringUtils.equals(member.name, PK_PROPERTY_NAME)) {
					if (value instanceof Item) {
						Item valueItem = (Item) item;

						saveInternal(valueItem, commit);

						// replace actual item with proxy
						value = createProxyModel(item);
					} else if (value.getClass().isArray()) {
						value = saveInternalCollection((Collection) Arrays.asList(value), commit);
					} else if (Collection.class.isAssignableFrom(value.getClass())) {
						value = saveInternalCollection((Collection) value, commit);
					} else if (Map.class.isAssignableFrom(value.getClass())) {
						// Map map = (Map) value;
						//
						// value = saveInternalCollection((Collection)
						// map.keySet(), commit);
						// value = saveInternalCollection((Collection)
						// map.values(), commit);
					}
				}

				entity.setProperty(member.name, value);
			}

			item.pk = storeEntity(entity, item.pk, item.getClass());
			// refresh(item);
			clearDirtyFlag(item);
		} catch (Exception e) {
			database.rollback();
		}

		if (commit)
			database.commit();
	}

	protected PK storeEntity(Entity entity, PK pk, Class<? extends Item> type) {
		// if (pk == null) {
		// pk = getDataStorageForType(type) getNextPk(type);
		// }
		//
		// entity.setPK(pk);
		getDataStorageForType(type).put(entity);

		return pk;
	}

	protected Collection<Item> saveInternalCollection(Collection<Item> items, boolean commit)
			throws IntrospectionException, CannotCreateModelProxyException, ModelNotUniqueException {

		Collection<Item> savedItems = new ArrayList<>();

		for (Item v : items) {
			if (v != null && v instanceof Item) {
				saveInternal(v, commit);
				savedItems.add(createProxyModel(v));
			}
		}

		return savedItems;
	}

	@Override
	public <T extends Item> T load(Class<T> type, long pk) throws ModelNotFoundException {
		Entity itemEntity = getDataStorageForType(type).get(pk);

		T item;
		try {
			item = type.newInstance();

			for (String property : itemEntity.getProperties().keySet()) {
				ClassUtil.setField(item, property, itemEntity.getProperty(property));
			}

			item.pk = itemEntity.getPK();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ModelNotFoundException(e);
		}

		return (T) item;
	}

	@Override
	public <T extends Item> List<T> load(Class<T> type, Map<String, Object> searchParameters) {

		List<T> foundItems = new ArrayList<>();

		Set<Long> pks = getDataStorageForType(type).get(searchParameters);

		for (Long pk : pks) {
			try {
				foundItems.add(load(type, pk));
			} catch (ModelNotFoundException e) {
				// ignore it for now
			}
			//
			// boolean found = true;
			//
			// for (String k : searchParameters.keySet()) {
			// Object v = searchParameters.get(k);
			//
			// if (!e.getProperty(k).equals(v)) {
			// found = false;
			// }
			// }
			//
			// if (found) {
			// try {
			// foundItems.add(load(type, e.getPK().longValue()));
			// } catch (ModelNotFoundException e1) {
			// loggingService.warn(String.format("Couldn't load item with
			// pk=%s", e.getPK().longValue()));
			// }
			// }
		}

		return foundItems;
	}

	@Override
	public <T extends Item> void refresh(T item) throws ModelNotFoundException {
		clearDirtyFlag(item);

		T loadedItem = load((Class<T>) item.getClass(), item.pk.longValue());

		for (ItemTypePropertyDefinition p : typeService.getItemTypeProperties(item.getClass()).values()) {
			item.setProperty(p.name, loadedItem.getProperty(p.name));
		}
	}

	protected void clearDirtyFlag(Item item) {
		ClassUtil.invokeMethod(item, "clearDirtyFlag");
	}

	@Override
	public <T extends Item> void loadProxyModel(T item) throws ModelNotFoundException {
		refresh(item);
	}

	@Override
	public <T extends Item> T createProxyModel(T item) throws CannotCreateModelProxyException {
		T proxyItem;
		try {
			proxyItem = (T) item.getClass().newInstance();
			ClassUtil.setField(proxyItem, "isProxy", true);

			proxyItem.pk = item.pk;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new CannotCreateModelProxyException(e);
		}

		return proxyItem;
	}

	@Override
	public void saveDataStorage() {
		database.close();
	}

	@Log(logLevel = LogLevel.DEBUG, message = "Clearing database ...")
	@Override
	public void clearDataStorage() {
		// for (BTreeMap<Long, Entity> m : dataStorage.values()) {
		// m.clear();
		// }
		//
		// saveDataStorage();
		// database.close();
		// initDataStorage();
	}

	protected static class DataStorage {
		private HTreeMap<Long, Entity> map = null;

		private Map<String, Map<Object, List<Long>>> indexes = new HashMap<>();
		private Long latestPkForType = null;;

		// private Class<? extends Item> itemType = null;

		public DataStorage(ItemTypeDefinition itemTypeDefinition,
				Collection<ItemTypePropertyDefinition> propertyDefinitions) {

			map = database.hashMap(itemTypeDefinition.typeClass).keySerializer(Serializer.LONG)
					.valueSerializer(Serializer.JAVA).createOrOpen();

			// try {
			// itemType = (Class<? extends Item>)
			// Class.forName(itemTypeDefinition.typeClass);
			// } catch (ClassNotFoundException e) {
			// //
			// }

			// for (ItemTypePropertyDefinition prop : propertyDefinitions) {
			// NavigableMap<Object, Long> indexMap = new HashMap<Object,
			// Long>();

			// NavigableSet<Object[]> propertyIndex = new TreeSet<Object[]>();
			//
			// Bind.secondaryKey(map, propertyIndex, new Fun.Function2<Boolean,
			// Integer, Person>() {
			// @Override
			// public Boolean run(Integer key, Person value) {
			// return Boolean.valueOf(value.isMale());
			// }
			// });
			//
			// }
		}

		public void indexEntity(Entity entity) {
			for (String prop : entity.getProperties().keySet()) {
				if (StringUtils.equalsIgnoreCase(prop, PK_PROPERTY_NAME)) {
					continue;
				}

				Object val = entity.getProperty(prop);

				Map<Object, List<Long>> values = indexes.get(prop);

				if (values == null) {
					values = new HashMap<>();
					indexes.put(prop, values);
				}

				if (val != null) {
					List<Long> indexedPKs = values.get(val);

					if (indexedPKs == null) {
						indexedPKs = new ArrayList<>();
						values.put(val, indexedPKs);
					}

					indexedPKs.add(entity.getPK().longValue());
				}
			}
		}

		public long getEntityCount() {
			long ret = 0;

			try {
				ret = map.values().size();
			} catch (DBException e) {
				// TODO: handle exception
			}

			return ret;
		}

		public void put(Entity value) {
			map.put(getNextPk(), value);
			indexEntity(value);
		}

		public Entity get(Long key) {
			return map.get(key);
		}

		public Set<Long> get(Map<String, Object> criteria) {
			Map<String, List<Long>> pksForProperty = new TreeMap<>();

			for (String k : criteria.keySet()) {
				Map<Object, List<Long>> values = indexes.get(k);

				if (values != null && values.values() != null) {
					pksForProperty.put(k, values.get(criteria.get(k)));
				}
			}

			Set<Long> commonPKs = intersection(new ArrayList<List<Long>>(pksForProperty.values()));

			return commonPKs;
		}

		public Collection<Entity> values() {
			return map.getValues();
		}

		public <T> Set<T> intersection(List<List<T>> list) {
			Set<T> result = Sets.newHashSet(list.get(0));
			for (List<T> numbers : list) {
				result = Sets.intersection(result, Sets.newHashSet(numbers));
			}
			return result;
		}

		protected synchronized Long getNextPk() {
			if (latestPkForType == null) {
				latestPkForType = new Long(getEntityCount());
			}

			// increase by one
			latestPkForType += 1;

			return latestPkForType;
		}
	}
}

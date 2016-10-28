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
			// database =
			// DBMaker.fileDB(configurationService.getString(CONFIG_KEY_STORAGE_FILE,
			// DEFAULT_DB_FILEPATH))
			// .fileMmapEnable().fileMmapPreclearDisable().cleanerHackEnable().transactionEnable()
			// .allocateStartSize(50 * 1024 * 1024).allocateIncrement(50 * 1024
			// * 1024).make();
			database = DBMaker.fileDB(configurationService.getString(CONFIG_KEY_STORAGE_FILE, DEFAULT_DB_FILEPATH))
					.transactionEnable().make();

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

		if (!model.isPersisted() && storage.get(model.uniquenessHash()) != null) {
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
	protected void saveInternal(Item item, boolean commit)
			throws IntrospectionException, ModelNotUniqueException, ModelSaveException {

		boolean unsavedChanges = !item.isPersisted() || item.isDirty();

		// if there is nothing to save, we return immediately
		// if (item.isPersisted() && !item.isDirty()) {
		// return;
		// }

		// check if there is already an item with the same unique properties
		if (!isUnique(item)) {
			throw new ModelNotUniqueException(
					"Cannot save model because there is already a model with the same uniqueness criteria");
		}

		// now iterate over all attributes and check for item references and
		// also save them
		try {
			Map<String, ItemTypePropertyDefinition> itemMembers = typeService.getItemTypeProperties(item.getClass());

			Entity entity = new Entity(item.pk != null ? item.pk.longValue() : null, item.getClass().getName(),
					item.uniquenessHash());

			for (ItemTypePropertyDefinition member : itemMembers.values()) {
				// ignore pk property
				if (!StringUtils.equalsIgnoreCase(member.name, PK_PROPERTY_NAME)) {
					Object value = modelService.getPropertyValue(item, member.name);

					if (value != null) {
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

					if (unsavedChanges) {
						entity.setProperty(member.name, value);
					}
				}
			}

			if (unsavedChanges) {
				item.pk = storeEntity(entity, item.pk, item.getClass());
				clearDirtyFlag(item);
			}
		} catch (Exception e) {
			database.rollback();
			throw new ModelSaveException("Could not save model", e);
		}

		if (commit)
			database.commit();
	}

	protected PK storeEntity(Entity entity, PK pk, Class<? extends Item> type) {
		if (pk != null) {
			entity.setPK(pk.longValue());
		}

		long newPk = getDataStorageForType(type).put(entity);

		return new PK(newPk, type);
	}

	protected Collection<Item> saveInternalCollection(Collection<Item> items, boolean commit)
			throws IntrospectionException, CannotCreateModelProxyException, ModelNotUniqueException,
			ModelSaveException {

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

			item.pk = new PK(itemEntity.getPK(), type);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ModelNotFoundException(e);
		}

		return (T) item;
	}

	@Override
	public <T extends Item> List<T> load(Class<T> type, Map<String, Comparable<?>> searchParameters) {

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

	@Override
	public void remove(PK pk) {
		getDataStorageForType(pk.getType()).remove(pk.longValue());
	}

	@Override
	public <T extends Item> void remove(T... items) {
		if (items.length > 0) {

			getDataStorageForType(items[0].getClass())
					.remove(Arrays.stream(items).mapToLong((i) -> i.pk.longValue()).toArray());
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

	/**
	 * This is an abstraction of the actual map db data storage.
	 */
	protected static class DataStorage {
		private HTreeMap<Long, Entity> map = null;
		private HTreeMap<Long, Long> uniqueIndex = null;

		private Map<String, Index> indexes = new HashMap<>();
		private Long latestPkForType = null;;

		ItemTypeDefinition typeDefinition = null;

		public DataStorage(ItemTypeDefinition itemTypeDefinition,
				Collection<ItemTypePropertyDefinition> propertyDefinitions) {

			this.typeDefinition = itemTypeDefinition;

			map = database.hashMap(itemTypeDefinition.typeClass).keySerializer(Serializer.LONG)
					.valueSerializer(Serializer.JAVA).createOrOpen();

			uniqueIndex = database.hashMap(itemTypeDefinition.typeClass + "UniqueIndex").keySerializer(Serializer.LONG)
					.valueSerializer(Serializer.LONG).createOrOpen();

		}

		public Entity get(Long key) {
			return map.get(key);
		}

		public Entity get(int uniqueHash) {
			Long pk = uniqueIndex.get(uniqueHash);

			if (pk != null) {
				return get(pk);
			}

			return null;
		}

		protected Index getIndex(String property) {
			String indexName = typeDefinition.typeCode + "." + property;

			Index propertyIndex = indexes.get(indexName);

			if (propertyIndex == null) {
				propertyIndex = new Index(indexName);
				indexes.put(indexName, propertyIndex);
			}

			return propertyIndex;
		}

		public Set<Long> get(Map<String, Comparable<?>> criteria) {
			Map<String, List<Long>> pksForProperty = new TreeMap<>();

			for (String k : criteria.keySet()) {
				pksForProperty.put(k, getIndex(k).getPk(criteria.get(k)));
			}

			Set<Long> commonPKs = intersection(new ArrayList<List<Long>>(pksForProperty.values()));

			return commonPKs;
		}

		public long getEntityCount() {
			long ret = 0;

			try {
				ret = map.values().size();
			} catch (DBException e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			return ret;
		}

		public long put(Entity entity) {
			if (entity.getPK() == null) {
				entity.setPK(getNextPk());
			}

			map.put(entity.getPK(), entity);

			updateUniquenessIndex(entity);
			indexEntity(entity);

			return entity.getPK();
		}

		public void remove(long... longValue) {
			for (long pk : longValue) {
				Entity e = get(pk);

				map.remove(pk);

				removeUniquenessIndex(e);
				removeIndexes(e);
			}
		}

		public Collection<Entity> values() {
			return map.getValues();
		}

		public void removeUniquenessIndex(Entity entity) {
			uniqueIndex.remove(entity.getUniquenessHash());
		}

		public void updateUniquenessIndex(Entity entity) {
			uniqueIndex.put(new Long(entity.getUniquenessHash()), entity.getPK());
		}

		private void removeIndexes(Entity e) {

		}

		/**
		 * Indexes all item properties.
		 */
		public void indexEntity(Entity entity) {
			for (String prop : entity.getProperties().keySet()) {
				if (StringUtils.equalsIgnoreCase(prop, PK_PROPERTY_NAME)) {
					continue;
				}

				Index index = getIndex(prop);

				Object propValue = entity.getProperty(prop);

				// don't index property values that are not comparables, like
				// collections
				if (propValue instanceof Comparable) {
					index.index((Comparable<?>) propValue, entity.getPK());
				}
			}
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

	protected static class Index {
		Map<Object, List<Long>> index;

		public Index(String name) {
			index = database.hashMap(name).keySerializer(Serializer.JAVA).valueSerializer(Serializer.JAVA)
					.createOrOpen();
		}

		public void index(Comparable<?> valueKey, Long pk) {
			List<Long> indexedPks = index.get(valueKey);

			if (indexedPks == null) {
				indexedPks = new ArrayList<>();
			}

			indexedPks.add(pk);

			index.put(valueKey, indexedPks);
		}

		public List<Long> getPk(Comparable<?> valueKey) {
			return index.get(valueKey);
		}
	}
}

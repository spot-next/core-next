package at.spot.core.persistence.service.impl;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.service.ConfigurationService;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.type.ItemTypeDefinition;
import at.spot.core.infrastructure.type.ItemTypePropertyDefinition;
import at.spot.core.infrastructure.type.PK;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.CannotCreateModelProxyException;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.persistence.service.impl.mapdb.Entity;
import at.spot.core.support.util.ClassUtil;

@Service
public class MapDBService implements PersistenceService {

	protected static final String CONFIG_KEY_STORAGE_FILE = "service.persistence.mapdb.filepath";
	protected static final String DEFAULT_DB_FILEPATH = "/var/tmp/storage.db";

	private DB database;
	private Map<String, HTreeMap<Long, Entity>> dataStorage = new HashMap<>();

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
				HTreeMap<Long, Entity> map = database.hashMap(t.typeClass).keySerializer(Serializer.LONG)
						.valueSerializer(Serializer.JAVA).createOrOpen();

				dataStorage.put(t.typeClass, map);
			}
		} catch (Exception e) {
			// org.mapdb.DBException$DataCorruption
			loggingService.error(e.getMessage());
		}
	}

	protected HTreeMap<Long, Entity> getDataStorageForType(Class<? extends Item> type) {
		return this.dataStorage.get(type.getName());
	}

	@Override
	public void save(Item model) throws ModelSaveException {
		try {
			saveInternal(model, true);
		} catch (IntrospectionException e) {
			throw new ModelSaveException(e);
		}
	}

	@Override
	public <T extends Item> void saveAll(T... models) throws ModelSaveException {
		saveAll(Arrays.asList(models));
	}

	@Override
	public <T extends Item> void saveAll(List<T> models) throws ModelSaveException {
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

	protected PK getNextPk(Class<? extends Item> type) {
		return new PK(getDataStorageForType(type).values().size() + 1, type);
	}

	protected void saveInternal(Item item, boolean commit) throws IntrospectionException {

		// if there is nothing to save, we return immedeiately
		if (item.isPersisted() && !item.isDirty()) {
			return;
		}

		// now iterate over all attributes and check for item references and
		// also save them
		try {
			Map<String, ItemTypePropertyDefinition> itemMembers = typeService.getItemTypeProperties(item.getClass());

			Entity entity = new Entity();

			for (ItemTypePropertyDefinition member : itemMembers.values()) {
				Object value = modelService.getPropertyValue(item, member.name);

				// ignore pk property
				if (value != null && !StringUtils.equals(member.name, "pk")) {
					if (value instanceof Item) {
						Item valueItem = (Item) item;

						saveInternal(valueItem, commit);

						ClassUtil.invokeMethod(value, "clearDirtyFlag");

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
		} catch (Exception e) {
			database.rollback();
		}

		if (commit)
			database.commit();
	}

	protected PK storeEntity(Entity entity, PK pk, Class<? extends Item> type) {
		HTreeMap<Long, Entity> storage = getDataStorageForType(type);

		if (pk == null) {
			pk = getNextPk(type);
		}

		entity.setPK(pk);
		storage.put(pk.longValue(), entity);

		return pk;
	}

	protected Collection<Item> saveInternalCollection(Collection<Item> items, boolean commit)
			throws IntrospectionException, CannotCreateModelProxyException {

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
		HTreeMap<Long, Entity> storage = getDataStorageForType(type);

		Entity itemEntity = storage.get(pk);

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
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public <T extends Item> void refresh(T item) throws ModelNotFoundException {
		T loadedItem = load((Class<T>) item.getClass(), item.pk.longValue());

		for (ItemTypePropertyDefinition p : typeService.getItemTypeProperties(item.getClass()).values()) {
			item.setProperty(p.name, loadedItem.getProperty(p.name));
		}
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

}

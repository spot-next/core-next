package at.spot.core.persistence.service.impl;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.mapdb.DB;
import org.mapdb.DBMaker;
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
import at.spot.core.infrastructure.type.LogLevel;
import at.spot.core.infrastructure.type.PK;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.CannotCreateModelProxyException;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.persistence.service.impl.mapdb.DataStorage;
import at.spot.core.persistence.service.impl.mapdb.Entity;
import at.spot.core.support.util.ClassUtil;

@Service
public class MapDBService implements PersistenceService {

	protected static final int MIN_ITEM_COUNT_FOR_PARALLEL_PROCESSING = 1000;

	public static final String CONFIG_KEY_STORAGE_FILE = "service.persistence.mapdb.filepath";
	public static final String DEFAULT_DB_FILEPATH = "/private/tmp/storage.db";

	public static final String PK_PROPERTY_NAME = "pk";

	static DB database;
	private final Map<String, DataStorage> dataStorage = new HashMap<>();

	@Autowired
	protected ModelService modelService;

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected LoggingService loggingService;

	@Autowired
	protected ConfigurationService configurationService;

	protected ForkJoinPool threadPool;

	@PostConstruct
	protected void init() {
		this.threadPool = new ForkJoinPool(10);
	}

	@Log(message = "Initializing MapDB storage ...")
	@Override
	public void initDataStorage() {
		try {
			database = DBMaker.fileDB(configurationService.getString(CONFIG_KEY_STORAGE_FILE, DEFAULT_DB_FILEPATH))
					.fileMmapEnable().fileMmapPreclearDisable().cleanerHackEnable().transactionEnable()
					.allocateStartSize(50 * 1024 * 1024).allocateIncrement(50 * 1024 * 1024).make();
			// database =
			// DBMaker.fileDB(configurationService.getString(CONFIG_KEY_STORAGE_FILE,
			// DEFAULT_DB_FILEPATH))
			// .transactionEnable().make();

			final Map<String, ItemTypeDefinition> itemTypes = typeService.getItemTypeDefinitions();

			// dataStorage =
			// database.hashMap("items").keySerializer(Serializer.LONG)
			// .valueSerializer(new ItemSerializer<>()).createOrOpen();

			for (final ItemTypeDefinition t : itemTypes.values()) {
				dataStorage.put(t.typeClass,
						new DataStorage(database, t, typeService.getItemTypeProperties(t.typeCode).values()));
			}
		} catch (final Exception e) {
			// org.mapdb.DBException$DataCorruption
			loggingService.error(e.getMessage());
		}

		loggingService.debug("MapDB service initialized");
	}

	protected DataStorage getDataStorageForType(final Class<? extends Item> type) {
		return this.dataStorage.get(type.getName());
	}

	/**
	 * Try to laod an item with the same unique properties. If there already is
	 * one stored, the given item is not unique.
	 * 
	 * @param model
	 * @return
	 */
	protected boolean isUnique(final Item model) {
		boolean isUnique = true;

		final DataStorage storage = getDataStorageForType(model.getClass());

		final Entity itemWithSameUniqueness = storage.get(model.uniquenessHash());

		if (itemWithSameUniqueness != null && !(itemWithSameUniqueness.getPK().equals(model.pk))) {
			isUnique = false;
		}

		return isUnique;
	}

	@Override
	public <T extends Item> void save(final T... models) throws ModelSaveException, ModelNotUniqueException {
		save(Arrays.asList(models));
	}

	@Override
	public <T extends Item> void save(final List<T> models) throws ModelSaveException, ModelNotUniqueException {
		final long start = System.currentTimeMillis();
		long duration = start;

		try {
			long i = 0;
			final int saveAfter = 100;

			for (final Item model : models) {
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

			saveDataStorage();
		} catch (final IntrospectionException e) {
			throw new ModelSaveException(e);
		}
	}

	// @Log(logLevel = LogLevel.DEBUG, measureTime = true, after = true)
	protected void saveInternal(final Item item, final boolean commit)
			throws IntrospectionException, ModelNotUniqueException, ModelSaveException {

		final boolean unsavedChanges = !item.isPersisted() || item.isDirty();

		// if there is nothing to save, we return immediately

		// check if there is already an item with the same unique properties
		if (!isUnique(item)) {
			throw new ModelNotUniqueException(
					"Cannot save model because there is already a model with the same uniqueness criteria");
		}

		// now iterate over all attributes and check for item references and
		// also save them
		try {
			final Map<String, ItemTypePropertyDefinition> itemMembers = typeService
					.getItemTypeProperties(item.getClass());

			final Entity entity = new Entity(item.pk != null ? item.pk : null, item.getClass().getName(),
					item.uniquenessHash());

			for (final ItemTypePropertyDefinition member : itemMembers.values()) {
				// ignore pk property
				if (!StringUtils.equalsIgnoreCase(member.name, PK_PROPERTY_NAME)) {
					Object value = modelService.getPropertyValue(item, member.name);

					if (value != null) {
						if (value instanceof Item) {
							final Item valueItem = item;

							saveInternal(valueItem, commit);

							// replace actual item with proxy
							value = createProxyModel(item);
						} else if (value.getClass().isArray()) {
							value = saveInternalCollection((Collection) Arrays.asList(value), commit);
						} else if (Collection.class.isAssignableFrom(value.getClass())) {
							value = saveInternalCollection((Collection) value, commit);
						} else if (Map.class.isAssignableFrom(value.getClass())) {
							// Map items = (Map) value;
							//
							// value = saveInternalCollection((Collection)
							// items.keySet(), commit);
							// value = saveInternalCollection((Collection)
							// items.values(), commit);
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
		} catch (final Exception e) {
			database.rollback();
			throw new ModelSaveException("Could not save model", e);
		}

		if (commit)
			database.commit();
	}

	protected Long storeEntity(final Entity entity, final Long pk, final Class<? extends Item> type) {
		if (pk != null) {
			entity.setPK(pk.longValue());
		}

		final long newPk = getDataStorageForType(type).put(entity);

		return newPk;
	}

	protected Collection<Item> saveInternalCollection(final Collection<Item> items, final boolean commit)
			throws IntrospectionException, CannotCreateModelProxyException, ModelNotUniqueException,
			ModelSaveException {

		final Collection<Item> savedItems = new ArrayList<>();

		for (final Item v : items) {
			if (v != null && v instanceof Item) {
				saveInternal(v, commit);
				savedItems.add(createProxyModel(v));
			}
		}

		return savedItems;
	}

	@Override
	public <T extends Item> T load(final Class<T> type, final long pk) throws ModelNotFoundException {
		final Entity itemEntity = getDataStorageForType(type).get(pk);

		T item;
		try {
			item = type.newInstance();

			for (final String property : itemEntity.getProperties().keySet()) {
				ClassUtil.setField(item, property, itemEntity.getProperty(property));
			}

			item.pk = itemEntity.getPK();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ModelNotFoundException(e);
		}

		return item;
	}

	@Override
	public <T extends Item> Stream<T> load(final Class<T> type, final Map<String, Comparable<?>> searchParameters) {
		return load(type, searchParameters, 0, 0, false, null);
	}

	@Override
	public <T extends Item> Stream<T> load(final Class<T> type, final Map<String, Comparable<?>> searchParameters,
			final int page, final int pageSize, final boolean loadAsProxy) {

		return load(type, searchParameters, page, pageSize, loadAsProxy, MIN_ITEM_COUNT_FOR_PARALLEL_PROCESSING);
	}

	@Override
	public <T extends Item> Stream<T> load(final Class<T> type, final Map<String, Comparable<?>> searchParameters,
			final int page, final int pageSize, final boolean loadAsProxy, final Integer minCountForParallelStream) {

		// prevent NPES
		Stream<T> foundItems = new ArrayList<T>().stream();

		try {
			foundItems = threadPool.submit(() -> {
				Stream<Long> stream = null;
				Set<Long> pks = null;

				if (searchParameters != null && !searchParameters.isEmpty()) {
					pks = getDataStorageForType(type).get(searchParameters);
				} else {
					pks = getDataStorageForType(type).getAll();
				}

				if (minCountForParallelStream != null && pks.size() >= minCountForParallelStream) {
					stream = pks.parallelStream();
				} else {
					stream = pks.stream();
				}

				if (pageSize > 0) {
					stream = stream.skip((page - 1) * pageSize).limit(pageSize);
				}

				Stream<T> retStream = stream.map((pk) -> {
					try {
						return load(type, pk);
					} catch (final ModelNotFoundException e1) {
						// ignore it for now
					}

					return null;
				});

				if (minCountForParallelStream != null && (pageSize >= minCountForParallelStream)) {
					retStream = retStream.parallel();
				}

				return retStream;
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			loggingService.exception("Can't load items", e);
		}

		return foundItems;
	}

	@Override
	public <T extends Item> void refresh(final T item) throws ModelNotFoundException {
		clearDirtyFlag(item);

		final T loadedItem = load((Class<T>) item.getClass(), item.pk.longValue());

		for (final ItemTypePropertyDefinition p : typeService.getItemTypeProperties(item.getClass()).values()) {
			item.setProperty(p.name, loadedItem.getProperty(p.name));
		}
	}

	@Override
	public void remove(final PK... pks) {
		for (final PK pk : pks) {
			getDataStorageForType(pk.getType()).remove(pk.longValue());
		}

		saveDataStorage();
	}

	@Override
	public <T extends Item> void remove(final T... items) {
		if (items.length > 0) {

			getDataStorageForType(items[0].getClass())
					.remove(Arrays.stream(items).mapToLong((i) -> i.pk.longValue()).toArray());
		}

		saveDataStorage();
	}

	protected void clearDirtyFlag(final Item item) {
		ClassUtil.invokeMethod(item, "clearDirtyFlag");
	}

	@Override
	public <T extends Item> void loadProxyModel(final T item) throws ModelNotFoundException {
		refresh(item);
	}

	@Override
	public <T extends Item> T createProxyModel(final T item) throws CannotCreateModelProxyException {
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
		database.commit();
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

	@PreDestroy
	private void shutdown() {
		database.commit();
		database.close();
	}
}

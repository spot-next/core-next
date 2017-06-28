package at.spot.core.persistence.service.impl;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.mapdb.DB;
import org.mapdb.DBException;
import org.mapdb.DBMaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.infrastructure.support.ItemTypeDefinition;
import at.spot.core.infrastructure.support.ItemTypePropertyDefinition;
import at.spot.core.infrastructure.support.LogLevel;
import at.spot.core.infrastructure.support.RelationProxyList;
import at.spot.core.infrastructure.type.PK;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.CannotCreateModelProxyException;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.exception.PersistenceStorageException;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.persistence.service.impl.mapdb.DataStorage;
import at.spot.core.persistence.service.impl.mapdb.Entity;
import at.spot.core.support.util.ClassUtil;
import at.spot.core.support.util.MiscUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
@Service
public class MapDBService extends AbstractService implements PersistenceService {

	protected static final int MIN_ITEM_COUNT_FOR_PARALLEL_PROCESSING = 1000;

	public static final String CONFIG_KEY_STORAGE_FILE = "service.persistence.mapdb.filepath";
	public static final String DEFAULT_DB_FILEPATH = "/private/tmp/storage.db";

	public static final String PK_PROPERTY_NAME = "pk";

	protected DB database;
	protected final Map<String, DataStorage> dataStorage = new HashMap<>();

	@Autowired
	protected ModelService modelService;

	@Autowired
	protected TypeService typeService;

	protected ForkJoinPool threadPool;

	@PostConstruct
	protected void init() {
		this.threadPool = new ForkJoinPool(10);
	}

	@Log(message = "Initializing MapDB storage ...")
	@Override
	public void initDataStorage() throws PersistenceStorageException {
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
				dataStorage.put(t.typeCode,
						new DataStorage(database, t, typeService.getItemTypeProperties(t.typeCode).values()));
			}
		} catch (final DBException e) {
			// org.mapdb.DBException$DataCorruption
			throw new PersistenceStorageException("Datastore is corrupt", e);
		} catch (final UnknownTypeException e) {
			loggingService.error(e.getMessage());
		}

		loggingService.debug("MapDB service initialized");
	}

	protected DataStorage getDataStorageForType(final String typeCode) {
		return this.dataStorage.get(typeCode);
	}

	/**
	 * Try to laod an item with the same unique properties. If there already is one
	 * stored, the given item is not unique.
	 * 
	 * @param model
	 * @return
	 */
	protected boolean isUnique(final Item model) {
		boolean isUnique = true;

		final DataStorage storage = getDataStorageForType(typeService.getTypeCode(model.getClass()));

		final Entity itemWithSameUniqueness = storage.get(model.uniquenessHash());

		if (itemWithSameUniqueness != null && !(itemWithSameUniqueness.getPK().equals(model.getPk()))) {
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

		try {
			long i = 0;
			final int saveAfter = 100;

			for (final Item model : models) {
				saveInternal(model, false);

				if (i > 0 && i % saveAfter == 0) {
					// database.commit();

					final long duration = System.currentTimeMillis() - start;
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

	@SuppressFBWarnings("REC_CATCH_EXCEPTION")
	// @Log(logLevel = LogLevel.DEBUG, measureTime = true, after = true)
	protected void saveInternal(final Item item, final boolean commit)
			throws IntrospectionException, ModelNotUniqueException, ModelSaveException {

		final boolean unsavedChanges = !item.isPersisted() || item.isDirty();

		// if there is nothing to save, we return immediately
		if (!unsavedChanges) {
			return;
		}

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

			final Entity entity = new Entity(item.getPk(), item.getClass().getName(), item.uniquenessHash());

			for (final ItemTypePropertyDefinition member : itemMembers.values()) {
				// ignore pk property and relation properties
				if (!StringUtils.equalsIgnoreCase(member.name, PK_PROPERTY_NAME)) {

					Object value = modelService.getPropertyValue(item, member.name);

					if (value != null) {

						// handle relation properties
						if (value instanceof RelationProxyList) {
							final RelationProxyList proxyList = ((RelationProxyList) value);

							saveInternalCollection(proxyList.getItemsToUpdate(), commit);
							remove(MiscUtil.<Item>toArray(proxyList.getItemsToRemove(), Item.class));
						}

						if (value instanceof Item) {
							final Item valueItem = (Item) value;

							saveInternal(valueItem, commit);

							// replace actual item with proxy
							value = createProxyModel(valueItem);
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
							loggingService.warn("Maps can't yet be persisted.");
						}
					}

					entity.setProperty(member.name, value);
				}
			}

			item.setPk(storeEntity(entity, item.getPk(), item.getClass()));
			resetModelState(item);
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

		final long newPk = getDataStorageForType(typeService.getTypeCode(type)).put(entity);

		return newPk;
	}

	protected Collection<Item> saveInternalCollection(final Collection<Item> items, final boolean commit)
			throws IntrospectionException, CannotCreateModelProxyException, ModelNotUniqueException,
			ModelSaveException {

		if (items != null) {
			final Collection<Item> savedItems = new ArrayList<>();

			for (final Item v : items) {
				if (v != null) {
					saveInternal(v, commit);
					savedItems.add(createProxyModel(v));
				}
			}

			return savedItems;
		}

		return null;
	}

	@Override
	public <T extends Item> Stream<T> load(final Class<T> type, final Map<String, Comparable<?>> searchParameters) {
		return load(type, searchParameters, 0, 0, false, null, false);
	}

	@Override
	public <T extends Item> Stream<T> load(final Class<T> type, final Map<String, Comparable<?>> searchParameters,
			final int page, final int pageSize, final boolean loadAsProxy) {

		return load(type, searchParameters, page, pageSize, loadAsProxy, MIN_ITEM_COUNT_FOR_PARALLEL_PROCESSING, false);
	}

	@Override
	public <T extends Item> Stream<T> load(final Class<T> type, final Map<String, Comparable<?>> searchParameters,
			final int page, final int pageSize, final boolean loadAsProxy, final Integer minCountForParallelStream,
			final boolean returnProxies) {

		// prevent NPES
		final List<T> foundItems = new ArrayList<T>();

		for (final T subtypeBean : getApplicationContext().getBeansOfType(type).values()) {
			final Class<T> subtype = (Class<T>) subtypeBean.getClass();

			try {
				final ForkJoinTask<Stream<T>> ret = threadPool.submit(() -> {
					Stream<Long> stream = null;
					Set<Long> pks = null;

					if (searchParameters != null && !searchParameters.isEmpty()) {
						final DataStorage data = getDataStorageForType(typeService.getTypeCode(subtype));
						if (data != null) {
							pks = data.get(searchParameters);
						} else {
							throw new PersistenceStorageException(
									String.format("Could not get datastorage for type %s", subtype));
							// loggingService.warn(String.format("Could not get
							// datastorage for type %s", type));
						}
					} else {
						final DataStorage storage = getDataStorageForType(typeService.getTypeCode(subtype));
						pks = storage != null ? storage.getAll() : Collections.emptySet();
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
							if (returnProxies) {
								return createProxyModel(subtype, pk);
							} else {
								return load(subtype, pk);
							}
						} catch (final ModelNotFoundException e1) {
							// ignore it for now
						}

						return null;
					});

					if (minCountForParallelStream != null && pageSize >= minCountForParallelStream) {
						retStream = retStream.parallel();
					}

					return retStream;
				});

				foundItems.addAll(ret.get().collect(Collectors.toList()));
			} catch (InterruptedException | ExecutionException e) {
				loggingService.exception("Can't load items", e);
				// throw new PersistenceStorageException("Can't load items from
				// storage.");
			}
		}

		return foundItems.stream();
	}

	@Override
	public <T extends Item> T load(final Class<T> type, final long pk) throws ModelNotFoundException {
		final T item = modelService.create(type);

		item.setPk(pk);
		refresh(item);

		return item;
	}

	@Override
	public <T extends Item> void refresh(final T item) throws ModelNotFoundException {
		resetModelState(item);

		final Entity itemEntity = getDataStorageForType(typeService.getTypeCode(item.getClass())).get(item.getPk());

		if (itemEntity == null) {
			throw new ModelNotFoundException(String.format("Model with pk=%s not found", item.getPk()));
		}

		if (itemEntity.getProperties() != null) {
			for (final String property : itemEntity.getProperties().keySet()) {
				ClassUtil.setField(item, property, itemEntity.getProperties().get(property));
			}
		}

		if (item.isPersisted()) {
			initRelationProperties(item);
		}

		loggingService.debug("Refreshed item");
	}

	/**
	 * Create new {@link RelationProxyList} when accessing a relation property
	 * 
	 * @param joinPoint
	 * @param rel
	 * @return
	 * @throws Throwable
	 */
	protected <T extends Item> void initRelationProperties(final T referencingItem) {
		for (final ItemTypePropertyDefinition p : typeService.getItemTypeProperties(referencingItem.getClass())
				.values()) {

			// if the property is a relation we setup a proxy relation list
			if (p.relationDefinition != null) {
				final Relation rel = ClassUtil.getAnnotation(referencingItem.getClass(), p.name, Relation.class);

				final List<Item> proxyList = new RelationProxyList<Item>(rel, referencingItem.getClass(),
						referencingItem.getPk(), typeService.isPropertyUnique(rel.referencedType(), rel.mappedTo()),
						p.name, () -> {
							referencingItem.markAsDirty(p.name);
						});

				ClassUtil.setField(referencingItem, p.name, proxyList);
			}
		}
	}

	@Override
	public void remove(final PK... pks) {
		for (final PK pk : pks) {
			getDataStorageForType(typeService.getTypeCode(pk.getType())).remove(pk.longValue());
		}

		saveDataStorage();
	}

	@Override
	public <T extends Item> void remove(final T... items) {
		if (items.length > 0) {

			getDataStorageForType(typeService.getTypeCode(items[0].getClass()))
					.remove(Arrays.stream(items).mapToLong((i) -> i.getPk().longValue()).toArray());
		}

		saveDataStorage();
	}

	protected void resetModelState(final Item item) {
		ClassUtil.invokeMethod(item, "clearDirtyFlag");
		ClassUtil.setField(item, "isProxy", false);
	}

	@Override
	public <T extends Item> void loadProxyModel(final T item) throws ModelNotFoundException {
		refresh(item);
	}

	@Override
	public <T extends Item> T createProxyModel(final T item) {
		return modelService.createProxyModel(item);
	}

	protected <T extends Item> T createProxyModel(final Class<T> type, final long pk) {
		return modelService.createProxyModel(type, pk);
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
		if (database != null) {
			database.commit();
			database.close();
		}
	}
}

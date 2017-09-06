package at.spot.core.persistence.service.impl.mapdb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.mapdb.DB;
import org.mapdb.DBException;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import com.google.common.collect.Sets;

import at.spot.core.infrastructure.support.ItemTypeDefinition;
import at.spot.core.infrastructure.support.ItemTypePropertyDefinition;
import at.spot.core.persistence.service.impl.MapDBService;

/**
 * This is an abstraction of the actual items db data storage.
 */
public class DataStorage {
	private HTreeMap<Long, Entity> items = null;
	private HTreeMap<Integer, Long> uniqueIndex = null;

	private final Map<String, Index> indexes = new HashMap<>();
	private Long latestPkForType = null;;

	private final DB database;

	ItemTypeDefinition typeDefinition = null;

	public DataStorage(final DB database, final ItemTypeDefinition itemTypeDefinition,
			final Collection<ItemTypePropertyDefinition> propertyDefinitions) {

		this.database = database;
		this.typeDefinition = itemTypeDefinition;

		items = database.hashMap(itemTypeDefinition.typeClass).keySerializer(Serializer.LONG)
				.valueSerializer(Serializer.JAVA).createOrOpen();

		uniqueIndex = database.hashMap(itemTypeDefinition.typeClass + "UniqueIndex").keySerializer(Serializer.INTEGER)
				.valueSerializer(Serializer.LONG).createOrOpen();
	}

	public synchronized Entity get(final Long key) {
		if (key != null) {
			return items.get(key);
		}

		return null;
	}

	public synchronized Entity get(final int uniqueHash) {
		// TODO: why do we have to use a long here although the map is defined
		// with integer keys?
		final Long pk = uniqueIndex.get(uniqueHash);

		if (pk != null) {
			return get(pk);
		}

		return null;
	}

	protected synchronized Index getIndex(final String property) {
		final String indexName = typeDefinition.typeCode + "." + property;

		Index propertyIndex = indexes.get(indexName);

		if (propertyIndex == null) {
			propertyIndex = new Index(database, indexName);
			indexes.put(indexName, propertyIndex);
		}

		return propertyIndex;
	}

	public synchronized Set<Long> getAll() {
		return items.getKeys();
	}

	public synchronized Set<Long> get(final Map<String, Comparable<?>> criteria) {
		final Map<String, List<Long>> pksForProperty = new TreeMap<>();

		boolean allValuesFound = true;

		for (final Map.Entry<String, Comparable<?>> entry : criteria.entrySet()) {
			final Comparable<?> v = entry.getValue();

			final List<Long> pk = getIndex(entry.getKey()).getPk(v);

			if (pk != null) {
				pksForProperty.put(entry.getKey(), pk);
			} else {
				// if there's nothing found, we don't have a valid AND join
				allValuesFound = false;
			}
		}

		Set<Long> commonPKs;

		if (allValuesFound) {
			commonPKs = intersection(new ArrayList<List<Long>>(pksForProperty.values()));
		} else {
			commonPKs = Collections.emptySet();
		}

		return commonPKs;
	}

	public synchronized long getEntityCount() {
		long ret = 0;

		try {
			ret = items.values().size();
		} catch (final DBException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return ret;
	}

	public synchronized long put(final Entity entity) {
		if (entity.getPK() == null) {
			entity.setPK(getNextPk());
		} else {
			removeUniquenessIndex(entity);
			removePropertyIndexes(entity);
		}

		items.put(entity.getPK(), entity);

		updateUniquenessIndex(entity);
		updatePropertyIndexes(entity);

		return entity.getPK();
	}

	public synchronized void remove(final long... longValue) {
		for (final long pk : longValue) {
			final Entity e = get(pk);

			items.remove(pk);

			removeUniquenessIndex(e);
			removePropertyIndexes(e);
		}
	}

	public synchronized Collection<Entity> values() {
		return items.getValues();
	}

	public synchronized void removeUniquenessIndex(final Entity entity) {
		for (final Integer id : uniqueIndex.getKeys()) {
			final Long val = uniqueIndex.get(id);

			if (val != null && entity != null && val.equals(entity.getPK())) {
				uniqueIndex.remove(id);
				break;
			}
		}
	}

	public synchronized void updateUniquenessIndex(final Entity entity) {
		// remove old indexes before
		// removeUniquenessIndex(entity);

		uniqueIndex.put(entity.getUniquenessHash(), entity.getPK());
	}

	public synchronized void removePropertyIndexes(final Entity entity) {
		if (entity != null) {
			for (final String prop : entity.getProperties().keySet()) {
				if (StringUtils.equalsIgnoreCase(prop, MapDBService.PK_PROPERTY_NAME)) {
					continue;
				}

				final Index index = getIndex(prop);

				index.removeIndex(entity.getPK());
			}
		}
	}

	/**
	 * Indexes all item properties.
	 */
	public synchronized void updatePropertyIndexes(final Entity entity) {
		// removePropertyIndexes(entity);

		for (final String prop : entity.getProperties().keySet()) {
			if (StringUtils.equalsIgnoreCase(prop, MapDBService.PK_PROPERTY_NAME)) {
				continue;
			}

			final Index index = getIndex(prop);

			final Object propValue = entity.getProperty(prop);

			// don't index property values that are not comparables, like
			// collections
			if (propValue instanceof Comparable) {
				index.index((Comparable<?>) propValue, entity.getPK());
			}
		}
	}

	public <T> Set<T> intersection(final List<List<T>> list) {
		if (list != null && list.size() > 0) {
			Set<T> result = Sets.newHashSet(list.get(0));

			for (final List<T> numbers : list) {
				result = Sets.intersection(result, Sets.newHashSet(numbers));
			}

			return result;
		} else {
			return Collections.emptySet();
		}
	}

	protected synchronized Long getNextPk() {
		if (latestPkForType == null) {
			latestPkForType = Long.valueOf(getEntityCount());
		}

		// increase by one
		latestPkForType += 1;

		return latestPkForType;
	}

}
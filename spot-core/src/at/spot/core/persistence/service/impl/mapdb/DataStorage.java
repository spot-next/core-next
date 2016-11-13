package at.spot.core.persistence.service.impl.mapdb;

import java.util.ArrayList;
import java.util.Collection;
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

import at.spot.core.infrastructure.type.ItemTypeDefinition;
import at.spot.core.infrastructure.type.ItemTypePropertyDefinition;
import at.spot.core.persistence.service.impl.MapDBService;

/**
 * This is an abstraction of the actual items db data storage.
 */
public class DataStorage {
	private HTreeMap<Long, Entity> items = null;
	private HTreeMap<Integer, Long> uniqueIndex = null;

	private Map<String, Index> indexes = new HashMap<>();
	private Long latestPkForType = null;;

	private DB database;

	ItemTypeDefinition typeDefinition = null;

	public DataStorage(DB database, ItemTypeDefinition itemTypeDefinition,
			Collection<ItemTypePropertyDefinition> propertyDefinitions) {

		this.database = database;
		this.typeDefinition = itemTypeDefinition;

		items = database.hashMap(itemTypeDefinition.typeClass).keySerializer(Serializer.LONG)
				.valueSerializer(Serializer.JAVA).createOrOpen();

		uniqueIndex = database.hashMap(itemTypeDefinition.typeClass + "UniqueIndex").keySerializer(Serializer.INTEGER)
				.valueSerializer(Serializer.LONG).createOrOpen();
	}

	public synchronized Entity get(Long key) {
		return items.get(key);
	}

	public synchronized Entity get(int uniqueHash) {
		// TODO: why do we have to use a long here although the map is defined
		// with integer keys?
		Long pk = uniqueIndex.get(uniqueHash);

		if (pk != null) {
			return get(pk);
		}

		return null;
	}

	protected synchronized Index getIndex(String property) {
		String indexName = typeDefinition.typeCode + "." + property;

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

	public synchronized Set<Long> get(Map<String, Comparable<?>> criteria) {
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
			ret = items.values().size();
		} catch (DBException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return ret;
	}

	public synchronized long put(Entity entity) {
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

	public synchronized void remove(long... longValue) {
		for (long pk : longValue) {
			Entity e = get(pk);

			items.remove(pk);

			removeUniquenessIndex(e);
			removePropertyIndexes(e);
		}
	}

	public Collection<Entity> values() {
		return items.getValues();
	}

	public synchronized void removeUniquenessIndex(Entity entity) {
		for (Integer id : uniqueIndex.getKeys()) {
			Long val = uniqueIndex.get(id);

			if (val != null && val.equals(entity.getPK())) {
				uniqueIndex.remove(id);
				break;
			}
		}
	}

	public synchronized void updateUniquenessIndex(Entity entity) {
		// remove old indexes before
		// removeUniquenessIndex(entity);

		uniqueIndex.put(entity.getUniquenessHash(), entity.getPK());
	}

	public synchronized void removePropertyIndexes(Entity entity) {
		for (String prop : entity.getProperties().keySet()) {
			if (StringUtils.equalsIgnoreCase(prop, MapDBService.PK_PROPERTY_NAME)) {
				continue;
			}

			Index index = getIndex(prop);

			index.removeIndex(entity.getPK());
		}
	}

	/**
	 * Indexes all item properties.
	 */
	public synchronized void updatePropertyIndexes(Entity entity) {
		// removePropertyIndexes(entity);

		for (String prop : entity.getProperties().keySet()) {
			if (StringUtils.equalsIgnoreCase(prop, MapDBService.PK_PROPERTY_NAME)) {
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
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
	private HTreeMap<Long, Long> uniqueIndex = null;

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

		uniqueIndex = database.hashMap(itemTypeDefinition.typeClass + "UniqueIndex").keySerializer(Serializer.LONG)
				.valueSerializer(Serializer.LONG).createOrOpen();
	}

	public Entity get(Long key) {
		return items.get(key);
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
			propertyIndex = new Index(database, indexName);
			indexes.put(indexName, propertyIndex);
		}

		return propertyIndex;
	}
	
	public Set<Long> getAll() {
		return items.getKeys();
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
			ret = items.values().size();
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

		items.put(entity.getPK(), entity);

		updateUniquenessIndex(entity);
		indexEntity(entity);

		return entity.getPK();
	}

	public void remove(long... longValue) {
		for (long pk : longValue) {
			Entity e = get(pk);

			items.remove(pk);

			removeUniquenessIndex(e);
			removeIndexes(e);
		}
	}

	public Collection<Entity> values() {
		return items.getValues();
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
package at.spot.core.persistence.service.impl.mapdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mapdb.DB;
import org.mapdb.Serializer;

public class Index {
	protected Map<Object, List<Long>> index;
	protected DB database;

	public Index(DB database, String name) {
		index = database.hashMap(name).keySerializer(Serializer.JAVA).valueSerializer(Serializer.JAVA).createOrOpen();
	}

	public synchronized void index(Comparable<?> valueKey, Long pk) {
		List<Long> indexedPks = index.get(valueKey);

		if (indexedPks == null) {
			indexedPks = new ArrayList<>();
		}

		indexedPks.add(pk);

		index.put(valueKey, indexedPks);
	}

	public synchronized void removeIndex(Long pk) {
		for (Object k : index.keySet()) {
			List<Long> val = index.get(k);

			if (val != null) {
				val.remove(pk);
				index.put(k, val);
			}
		}
	}

	public List<Long> getPk(Comparable<?> valueKey) {
		return index.get(valueKey);
	}
}
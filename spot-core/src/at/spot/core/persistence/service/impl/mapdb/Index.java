package at.spot.core.persistence.service.impl.mapdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mapdb.DB;
import org.mapdb.Serializer;

public class Index {
	protected Map<Object, List<Long>> index;

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
		for (Map.Entry<Object, List<Long>> entry : index.entrySet()) {
			List<Long> val = entry.getValue();

			if (val != null) {
				val.remove(pk);
				index.put(entry.getKey(), val);
			}
		}
	}

	public List<Long> getPk(Comparable<?> valueKey) {
		return index.get(valueKey);
	}
}
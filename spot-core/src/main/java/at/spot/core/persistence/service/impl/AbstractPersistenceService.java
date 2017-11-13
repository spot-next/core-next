package at.spot.core.persistence.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.infrastructure.support.ItemTypePropertyDefinition;
import at.spot.core.model.Item;
import at.spot.core.persistence.service.PersistenceService;

public abstract class AbstractPersistenceService extends AbstractService implements PersistenceService {

	@Autowired
	protected TypeService typeService;

	@Override
	public <T extends Item> Map<String, Object> convertItemToMap(T item) {
		Map<String, ItemTypePropertyDefinition> properties = typeService.getItemTypeProperties(item.getClass());

		final ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.convertValue(item, new TypeReference<Map<String, Object>>() {
		});

		Map<String, Object> retMap = new HashMap<>();

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (properties.get(entry.getKey()) != null) {
				if (entry.getValue() != null) {
					if (entry.getValue() instanceof Comparable) {
						retMap.put(entry.getKey(), (Comparable<?>) entry.getValue());
					} else if (entry.getValue() instanceof Collection || entry.getValue() instanceof Map) {
						loggingService.warn(String.format(
								"Item property '%s' is a list or collection - it will be ignored.", entry.getKey()));
					}
				}
			}
		}

		return retMap;
	}
}

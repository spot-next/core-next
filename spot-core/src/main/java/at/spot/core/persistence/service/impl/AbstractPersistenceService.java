package at.spot.core.persistence.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
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
	public <T extends Item> Map<String, Object> convertItemToMap(final T item) {
		final Map<String, ItemTypePropertyDefinition> properties = typeService.getItemTypeProperties(item.getClass());

		final ObjectMapper mapper = new ObjectMapper();
		final Map<String, Object> map = mapper.convertValue(item, new TypeReference<Map<String, Object>>() {
		});

		final Map<String, Object> retMap = new HashMap<>();

		for (final Map.Entry<String, ItemTypePropertyDefinition> prop : properties.entrySet()) {
			if (isArrayOrCollection(prop.getValue().getReturnType())) {
				loggingService.warn(String.format("Item property '%s' is a list or collection - it will be ignored.",
						prop.getValue().getName()));
			} else {

				try {
					final Object value = PropertyUtils.getProperty(item, prop.getValue().getName());

					if (value != null) {
						retMap.put(prop.getValue().getName(), value);
					}
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					loggingService.warn(String.format(
							"Could not convert property '%s' for given item of type '%s' - it will be ignored.",
							prop.getValue().getName(), item.getClass().getSimpleName()));
				}
			}
		}

		return retMap;
	}

	protected boolean isArrayOrCollection(final Class<?> type) {
		return type.isArray() || Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
	}
}
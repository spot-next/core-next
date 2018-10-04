package io.spotnext.core.persistence.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.exception.UnknownTypeException;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.infrastructure.support.Log;
import io.spotnext.core.persistence.service.PersistenceService;
import io.spotnext.infrastructure.type.Item;
import io.spotnext.infrastructure.type.ItemTypePropertyDefinition;

/**
 * <p>Abstract AbstractPersistenceService class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
public abstract class AbstractPersistenceService extends AbstractService implements PersistenceService {

	@Autowired
	protected TypeService typeService;

	/** {@inheritDoc} */
	@Override
	public <T extends Item> Map<String, Object> convertItemToMap(final T item) {
		final String typeCode = typeService.getTypeCodeForClass(item.getClass());
		final Map<String, Object> retMap = new HashMap<>();

		try {
			final Map<String, ItemTypePropertyDefinition> properties = typeService.getItemTypeDefinition(typeCode)
					.getProperties();

			for (final Map.Entry<String, ItemTypePropertyDefinition> prop : properties.entrySet()) {
				if (isArrayCollectionOrMap(prop.getValue().getReturnType())) {
					Log
							.warn(String.format("Item property '%s' is a list or collection - it will be ignored.",
									prop.getValue().getName()));
				} else {
					try {
						final Object value = PropertyUtils.getProperty(item, prop.getValue().getName());

						if (value instanceof Item) {
							Log.debug(String.format(
									"Item property '%s' is an Item type (%s) - this will not work if the object is unsafed.",
									prop.getValue().getName(), value.getClass().getName()));
						}

						if (value != null) {
							retMap.put(prop.getValue().getName(), value);
						}
					} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						Log.warn(String.format(
								"Could not convert property '%s' for given item of type '%s' - it will be ignored.",
								prop.getValue().getName(), item.getClass().getSimpleName()));
					}
				}
			}
		} catch (final UnknownTypeException e1) {
			Log.warn(String.format("Could not load properties for item with type code '%s'", typeCode));
		}

		return retMap;
	}

	protected boolean isArrayCollectionOrMap(final Class<?> type) {
		return type.isArray() || Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
	}
}

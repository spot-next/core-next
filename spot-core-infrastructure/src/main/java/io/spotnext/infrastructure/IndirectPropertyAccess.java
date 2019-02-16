package io.spotnext.infrastructure;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

import io.spotnext.infrastructure.annotation.Property;
import io.spotnext.support.util.ClassUtil;

/**
 * Provides default functionality to indirect (reflection-based) access to fields annotated with {@link Property}.
 */
public interface IndirectPropertyAccess {
	/**
	 * Returns all fields annotated with the {@link Property} annotation.
	 * 
	 * @param filter can be null or a predicate that further filters the returned item properties.
	 * @return all filtered item properties
	 */
	default Map<String, Object> getProperties(BiPredicate<Field, Object> filter) {
		final Map<String, Object> props = new HashMap<>();

		for (final Field field : ClassUtil.getFieldsWithAnnotation(this.getClass(), Property.class)) {
			final Object propertyValue = ClassUtil.getField(this, field.getName(), true);

			if (filter == null || filter.test(field, propertyValue)) {
				props.put(field.getName(), propertyValue);
			}
		}

		return props;
	}

	/**
	 * Returns all fields name/value pairs annotated with the {@link Property} annotation.
	 * 
	 * @return all item properties
	 */
	default Map<String, Object> getProperties() {
		return getProperties(null);
	}

	/**
	 * Gets the property value for the given property name.
	 * 
	 * @param propertyName of the field to get the value from
	 * @return the field value
	 */
	default Object get(String propertyName) {
		return ClassUtil.getProperty(this, propertyName);
	}

	/**
	 * Sets the given property value. Unknown properties are ignored.
	 * 
	 * @param propertyName the name of the property to write to
	 * @param value        the property value
	 */
	default void set(String propertyName, Object value) {
		ClassUtil.setProperty(this, propertyName, value);
	}
}

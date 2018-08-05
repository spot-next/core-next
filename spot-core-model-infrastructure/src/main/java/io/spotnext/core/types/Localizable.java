package io.spotnext.core.types;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.support.util.ClassUtil;
import io.spotnext.core.support.util.MiscUtil;

public interface Localizable<R> {

	/**
	 * Returns the field value for the given locale. The field name is being created
	 * out of the locale, eg. en_GB.
	 */
	default R get(final Locale locale) {
		return (R) ClassUtil.getField(this, locale.toString(), true);
	}

	/**
	 * @see Localizable#get(Locale). The default locale will be used.
	 */
	default R get() {
		Locale locale = Locale.getDefault();
		return (R) ClassUtil.getField(this, locale.toString(), true);
	}

	/**
	 * Sets the given value to the localized field. The field name is being created
	 * out of the locale, eg. en_GB.
	 */
	default void set(final Locale locale, final R value) {
		ClassUtil.setField(this, locale.toString(), value);
	}

	/**
	 * @see Localizable#set(Locale, Object). The default locale will be used.
	 */
	default void set(final R value) {
		Locale locale = Locale.getDefault();
		ClassUtil.setField(this, locale.toString(), value);
	}

	@JsonAnyGetter
	@JsonProperty
	default Map<Locale, R> getValues() {
		Map<Locale, R> values = new HashMap<>();

		Set<Field> properties = ClassUtil.getFieldsWithAnnotation(this.getClass(), Property.class);

		for (Field f : properties) {
			try {
				Locale locale = MiscUtil.parseLocale(f.getName());
				Object value = ClassUtil.getField(this, f.getName(), true);

				if (value != null) {
					values.put(locale, (R) value);
				}
			} catch (IllegalArgumentException | IllegalStateException e) {
				throw new IllegalStateException("Could not read localized field value", e);
			}
		}

		return values;
	}

	@JsonAnySetter
	default void setValues(Map<Locale, R> values) {
		if (values != null) {
			values.entrySet().forEach(e -> set(e.getKey(), e.getValue()));
		}
	}
}

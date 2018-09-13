package io.spotnext.infrastructure.type;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.spotnext.infrastructure.annotation.Property;
import io.spotnext.support.util.ClassUtil;
import io.spotnext.support.util.MiscUtil;

public interface Localizable<R> {

	/**
	 * Returns the field value for the given locale. The field name is being created
	 * out of the locale, eg. en_GB.
	 * 
	 * @param locale the locale of the desired value
	 * @return the value for the given locale or null
	 */
	default R get(final Locale locale) {
		return (R) ClassUtil.getField(this, locale.toString(), true);
	}

	/**
	 * See also {@link Localizable#get(Locale)}. The default locale will be used.
	 * 
	 * @return the value for the {@link Locale#getDefault()} locale or null.
	 */
	default R get() {
		Locale locale = Locale.getDefault();
		return (R) ClassUtil.getField(this, locale.toString(), true);
	}

	/**
	 * Sets the given value to the localized field. The field name is being created
	 * out of the locale, eg. en_GB.
	 * 
	 * @param locale the locale of the given value
	 * @param value  the value to be localized
	 */
	default void set(final Locale locale, final R value) {
		ClassUtil.setField(this, locale.toString(), value);
	}

	/**
	 * See also {@link Localizable#set(Locale, Object)}. The default locale will be
	 *      used.
	 * 
	 * @param value the value that will be stored localized with the
	 *              {@link Locale#getDefault()} locale.
	 */
	default void set(final R value) {
		Locale locale = Locale.getDefault();
		ClassUtil.setField(this, locale.toString(), value);
	}

	/**
	 * @return all localized values.
	 */
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

	/**
	 * Adds all localized values to the existing one (=merge).
	 * 
	 * @param values the values to merge into the existing values, possibly
	 *               overwriting some values.
	 */
	@JsonAnySetter
	default void setValues(Map<Locale, R> values) {
		if (values != null) {
			values.entrySet().forEach(e -> set(e.getKey(), e.getValue()));
		}
	}
}

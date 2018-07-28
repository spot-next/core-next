package at.spot.core.types;

import java.util.Locale;

import at.spot.core.support.util.ClassUtil;

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
}

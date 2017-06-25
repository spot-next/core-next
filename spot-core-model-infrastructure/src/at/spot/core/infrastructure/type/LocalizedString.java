package at.spot.core.infrastructure.type;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LocalizedString implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	final Map<Locale, String> strings = new HashMap<>();;

	public LocalizedString() {
	}

	public LocalizedString(final String string) {
		this(string, getDefaultLocale());
	}

	public LocalizedString(final String string, final Locale locale) {
		strings.put(locale, string);
	}

	/**
	 * Returns the string of the default locale.
	 */
	public String get() {
		return get(getDefaultLocale());
	}

	/**
	 * Returns the string for the given locale.
	 */
	public String get(final Locale locale) {
		return strings.get(locale);
	}

	/**
	 * Sets the string of the default locale.
	 */
	public String set(final String string) {
		return strings.put(getDefaultLocale(), string);
	}

	/**
	 * Sets the string for the given locale.
	 */
	public String get(final String string, final Locale locale) {
		return strings.put(locale, string);
	}

	/**
	 * Returns the default locale, provided by the {@link I18nService}.
	 * 
	 * @return
	 */
	protected static Locale getDefaultLocale() {
		return Locale.getDefault();
	}

	@Override
	public String toString() {
		final StringBuilder ret = new StringBuilder();

		for (final Map.Entry<Locale, String> entry : strings.entrySet()) {
			ret.append(String.format("%s=%s", entry.getKey(), strings.get(entry.getKey())) + "\n");
		}

		return ret.toString();
	}
}

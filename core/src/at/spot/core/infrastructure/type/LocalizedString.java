package at.spot.core.infrastructure.type;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import at.spot.core.infrastructure.service.I18nService;

public class LocalizedString implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	Map<Locale, String> strings;

	public LocalizedString(String string) {
		this(string, getDefaultLocale());
	}

	public LocalizedString(String string, Locale locale) {
		strings = new HashMap<>();

		for (Locale l : DateFormat.getAvailableLocales()) {
			strings.put(l, null);
		}
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
	public String get(Locale locale) {
		return strings.get(locale);
	}

	/**
	 * Returns the default locale, provided by the {@link I18nService}.
	 * 
	 * @return
	 */
	protected static Locale getDefaultLocale() {
		// TODO: read default locale from i18nservice
		return Locale.ENGLISH;
	}

	@Override
	public String toString() {
		String ret = "";

		for (Locale l : strings.keySet()) {
			ret += String.format("%s=%s\n", l, strings.get(l));
		}

		return ret;
	}

}

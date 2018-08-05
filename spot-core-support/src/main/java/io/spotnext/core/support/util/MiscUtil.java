package io.spotnext.core.support.util;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import io.spotnext.core.support.exception.UnsupportedLocale;

@SuppressWarnings({ "unchecked", "REC_CATCH_EXCEPTION" })
public class MiscUtil {

	/**
	 * Calls close() on all given objects that implement the {@link Closeable}
	 * interface. Doesn't throw any exceptions at all.
	 *
	 * @param closableObject
	 */
	public static void closeQuietly(final Closeable... closableObjects) {
		for (final Closeable c : closableObjects) {
			try {
				if (c != null) {
					c.close();
				}
			} catch (final IOException e) {
				// LOG.log(Level.WARN, String.format("An error occured while closing %s",
				// e.get));
			}
		}
	}

	public static int intOrDefault(final String value, final int defaultValue) {
		if (StringUtils.isNotBlank(value)) {
			return Integer.parseInt(value);
		}

		return defaultValue;
	}

	public static long longOrDefault(final String value, final long defaultValue) {
		if (StringUtils.isNotBlank(value)) {
			return Long.parseLong(value);
		}

		return defaultValue;
	}

	public static double doubleOrDefault(final String value, final double defaultValue) {
		if (StringUtils.isNotBlank(value)) {
			return Double.parseDouble(value);
		}

		return defaultValue;
	}

	public static float floatOrDefault(final String value, final float defaultValue) {
		if (StringUtils.isNotBlank(value)) {
			return Float.parseFloat(value);
		}

		return defaultValue;
	}

	public static String removeEnclosingQuotes(final String string) {
		return string.replaceAll("^\"|\"$", "");
	}

	public static <T> T[] toArray(final Collection<T> collection, final Class<T> arrayType) {
		T[] ret = null;

		ret = collection.toArray((T[]) Array.newInstance(arrayType, 0));

		return ret;
	}

	/**
	 * @throws IllegalStateException if the locale can be parsed but is not
	 *                               available/valid.
	 */
	public static Locale parseLocale(String localeString) throws IllegalStateException {
		Locale locale = null;

		if (StringUtils.isNotBlank(localeString)) {
			String[] splitLocaleString = localeString.split("_");

			if (splitLocaleString.length == 1) {
				locale = new Locale(localeString);
			} else if (splitLocaleString.length == 2) {
				locale = new Locale(splitLocaleString[0], splitLocaleString[1]);
			}

			if (!LocaleUtils.isAvailableLocale(locale)) {
				throw new IllegalStateException(String.format("Unknown locale %s", locale));
			}
		}

		return locale;
	}

	/**
	 * @param locale the locale that doesn't contain a country part, eg. for
	 *               {@link Locale#ENGLISH}
	 * @return a the corresponding locale with country part, eg. {@link Locale#UK}
	 * @throws UnsupportedLocale when there is no country locale defined for the
	 *                           given locale.
	 */
	public static Locale getCountryLocale(Locale locale) throws UnsupportedLocale {
		Locale ret = locale;
		if (StringUtils.isBlank(locale.getCountry())) {
			if (Locale.ENGLISH.equals(locale)) {
				ret = Locale.UK;
			} else if (Locale.GERMAN.equals(locale)) {
				ret = locale.GERMANY;
			} else if (Locale.FRENCH.equals(locale)) {
				ret = locale.FRANCE;
			} else if (Locale.ITALIAN.equals(locale)) {
				ret = locale.ITALY;
			} else if (Locale.CHINESE.equals(locale)) {
				ret = locale.CHINA;
			} else if (Locale.JAPANESE.equals(locale)) {
				ret = locale.JAPAN;
			} else if (Locale.KOREAN.equals(locale)) {
				ret = locale.KOREA;
			} else if (Locale.forLanguageTag("es").equals(locale)) {
				ret = new Locale(locale.getLanguage(), "ES");
			} else {
				throw new UnsupportedLocale(locale);
			}
		}

		return ret;
	}
}

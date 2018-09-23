package io.spotnext.support.exception;

import java.util.Locale;

/**
 * <p>UnsupportedLocale class.</p>
 *
 * @since 1.0
 * @author mojo2012
 * @version 1.0
 */
public class UnsupportedLocale extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for UnsupportedLocale.</p>
	 *
	 * @param locale a {@link java.util.Locale} object.
	 */
	public UnsupportedLocale(Locale locale) {
		super(String.format("There is no country locale defined for the given locale %s", locale));
	}
}

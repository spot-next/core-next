package io.spotnext.core.support.exception;

import java.util.Locale;

public class UnsupportedLocale extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnsupportedLocale(Locale locale) {
		super(String.format("There is no country locale defined for the given locale %s", locale));
	}
}

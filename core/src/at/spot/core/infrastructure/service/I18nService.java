package at.spot.core.infrastructure.service;

import java.util.Locale;

public interface I18nService {

	/**
	 * Returns the default locale - configured in the application properties.
	 */
	Locale getDefaultLocale();
}

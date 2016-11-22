package at.spot.core.infrastructure.service;

import java.util.Currency;
import java.util.Locale;
import java.util.Set;

public interface I18nService {

	/**
	 * Returns the default locale - configured in the application properties.
	 */
	Locale getDefaultLocale();

	/**
	 * Returns the currently used locale, either defined by
	 * {@link #getDefaultLocale()} or by the system.
	 * 
	 * @return
	 */
	Locale getCurrentLocale();

	/**
	 * Returns all available locales.
	 * 
	 * @return
	 */
	Set<Locale> getAllAvailableLocales();

	/**
	 * Returns the default currency - configured in the application properties.
	 * 
	 * @return
	 */
	Currency getDefaultCurrency();

	/**
	 * Returns the currently used currency, either defined by
	 * {@link #getDefaultCurrency()} or by the system.
	 * 
	 * @return
	 */
	Currency getCurrentCurrency();

	/**
	 * Returns all available currencies.
	 * 
	 * @return
	 */
	Set<Currency> getAllAvailableCurrencies();
}

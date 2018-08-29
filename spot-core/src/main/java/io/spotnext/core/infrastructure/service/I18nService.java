package io.spotnext.core.infrastructure.service;

import java.util.Currency;
import java.util.Locale;
import java.util.Set;

import org.springframework.context.i18n.LocaleContextHolder;

/**
 * <p>I18nService interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface I18nService {

	/**
	 * Returns the default locale - configured in the application properties.
	 * This locale is also set as java default locale:
	 * {@link java.util.Locale#setDefault(Locale)}
	 *
	 * @return a {@link java.util.Locale} object.
	 */
	Locale getDefaultLocale();

	/**
	 * Returns the locale set as default in the current thread. See
	 * {@link org.springframework.context.i18n.LocaleContextHolder#getLocale()}.
	 *
	 * @return a {@link java.util.Locale} object.
	 */
	Locale getCurrentLocale();

	/**
	 * Returns all available locales.
	 *
	 * @return a {@link java.util.Set} object.
	 */
	Set<Locale> getAllAvailableLocales();

	/**
	 * Returns the default currency - configured in the application properties.
	 *
	 * @return a {@link java.util.Currency} object.
	 */
	Currency getDefaultCurrency();

	/**
	 * Returns the currently used currency, either defined by
	 * {@link #getDefaultCurrency()} or by the system.
	 *
	 * @return a {@link java.util.Currency} object.
	 */
	Currency getCurrentCurrency();

	/**
	 * Returns all available currencies.
	 *
	 * @return a {@link java.util.Set} object.
	 */
	Set<Currency> getAllAvailableCurrencies();
}

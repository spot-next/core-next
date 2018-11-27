package io.spotnext.core.infrastructure.service.impl;

import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.service.I18nService;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.core.infrastructure.support.spring.PostConstructor;

/**
 * <p>DefaultI18nService class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class DefaultI18nService extends AbstractService implements I18nService, PostConstructor {

	/** Constant <code>DEFAULT_LOCALE_KEY="i18n.default.locale"</code> */
	public static final String DEFAULT_LOCALE_KEY = "i18n.default.locale";
	/** Constant <code>DEFAULT_CURRENCY_KEY="i18n.default.currency"</code> */
	public static final String DEFAULT_CURRENCY_KEY = "i18n.default.currency";

	/** Constant <code>DEFAULT_CURRENCY="EUR"</code> */
	public static final String DEFAULT_CURRENCY = "EUR";

	protected Currency defaultCurrency = null;

	/**
	 * <p>init.</p>
	 */
	@Override
	public void setup() {
		final String loc = configurationService.getString(DEFAULT_LOCALE_KEY);

		Locale defaultLocale = Locale.ENGLISH;

		if (StringUtils.isNotBlank(loc)) {
			defaultLocale = LocaleUtils.toLocale(loc);
		}

		Locale.setDefault(defaultLocale);
		LocaleContextHolder.setLocale(defaultLocale);

		final String currencyIso = configurationService.getString(DEFAULT_CURRENCY_KEY);

		// try to use the default currency defined in the properties
		if (StringUtils.isNotBlank(currencyIso)) {
			try {
				defaultCurrency = Currency.getInstance(currencyIso);
			} catch (final IllegalArgumentException e) {
				Logger.error(String.format("Could not set default locale %s", currencyIso));
			}
		}

		// use hardcoded default in error cases
		if (defaultCurrency == null) {
			defaultCurrency = Currency.getInstance(DEFAULT_CURRENCY);
		}
	}

	/** {@inheritDoc} */
	@Override
	public Locale getDefaultLocale() {
		return Locale.getDefault();
	}

	/** {@inheritDoc} */
	@Override
	public Locale getCurrentLocale() {
		return LocaleContextHolder.getLocale();
	}

	/** {@inheritDoc} */
	@Override
	public Set<Locale> getAllAvailableLocales() {
		return new HashSet<Locale>(Arrays.asList(Locale.getAvailableLocales()));
	}

	/** {@inheritDoc} */
	@Override
	public Currency getDefaultCurrency() {
		return defaultCurrency;
	}

	/** {@inheritDoc} */
	@Override
	public Currency getCurrentCurrency() {
		return defaultCurrency;
	}

	/** {@inheritDoc} */
	@Override
	public Set<Currency> getAllAvailableCurrencies() {
		return Currency.getAvailableCurrencies();
	}

}

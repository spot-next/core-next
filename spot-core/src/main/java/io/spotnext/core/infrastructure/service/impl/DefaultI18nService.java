package io.spotnext.core.infrastructure.service.impl;

import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.service.I18nService;

@Service
public class DefaultI18nService extends AbstractService implements I18nService {

	public static final String DEFAULT_LOCALE_KEY = "i18n.default.locale";
	public static final String DEFAULT_CURRENCY_KEY = "i18n.default.currency";

	public static final String DEFAULT_CURRENCY = "EUR";

	protected Currency defaultCurrency = null;

	@PostConstruct
	public void init() {
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
				loggingService.error(String.format("Could not set default locale %s", currencyIso));
			}
		}

		// use hardcoded default in error cases
		if (defaultCurrency == null) {
			defaultCurrency = Currency.getInstance(DEFAULT_CURRENCY);
		}
	}

	@Override
	public Locale getDefaultLocale() {
		return Locale.getDefault();
	}

	@Override
	public Locale getCurrentLocale() {
		return LocaleContextHolder.getLocale();
	}

	@Override
	public Set<Locale> getAllAvailableLocales() {
		return new HashSet<Locale>(Arrays.asList(Locale.getAvailableLocales()));
	}

	@Override
	public Currency getDefaultCurrency() {
		return defaultCurrency;
	}

	@Override
	public Currency getCurrentCurrency() {
		return defaultCurrency;
	}

	@Override
	public Set<Currency> getAllAvailableCurrencies() {
		return Currency.getAvailableCurrencies();
	}

}

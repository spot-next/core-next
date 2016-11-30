package at.spot.core.infrastructure.service.impl;

import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.service.ConfigurationService;
import at.spot.core.infrastructure.service.I18nService;

@Service
public class DefaultI18nService implements I18nService {

	public static final String DEFAULT_LOCALE_KEY = "i18n.default.locale";

	@Autowired
	protected ConfigurationService configurationService;

	protected Locale defaultLocale = Locale.ENGLISH;
	protected Currency defaultCurrency = null;

	@PostConstruct
	public void init() {
		final String loc = configurationService.getString(DEFAULT_LOCALE_KEY);

		if (StringUtils.isNotBlank(loc)) {
			final Locale locale = new Locale(loc);
			defaultLocale = locale;
		}
	}

	@Override
	public Locale getDefaultLocale() {
		return defaultLocale;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Currency> getAllAvailableCurrencies() {
		return Currency.getAvailableCurrencies();
	}

	/*
	 * Spring injection
	 */

}

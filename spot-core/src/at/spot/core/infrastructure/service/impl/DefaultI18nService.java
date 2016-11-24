package at.spot.core.infrastructure.service.impl;

import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.service.I18nService;

@Service
public class DefaultI18nService implements I18nService {

	protected Locale defaultLocale = Locale.ENGLISH;
	protected Currency defaultCurrency = null;

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

	public void setDefaultLocale(final Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public void setDefaultCurrency(final Currency defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}
}

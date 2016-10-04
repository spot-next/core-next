package at.spot.core.infrastructure.service.impl;

import java.util.Locale;

import org.springframework.stereotype.Component;

import at.spot.core.infrastructure.service.I18nService;

@Component
public class DefaultI18nService implements I18nService {

	@Override
	public Locale getDefaultLocale() {
		return null;
	}

}

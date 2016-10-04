package at.spot.core.infrastructure.service.impl;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import at.spot.core.infrastructure.service.I18nService;
import at.spot.core.infrastructure.service.L10nService;

@Component
public class DefaultL10nService implements L10nService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	I18nService i18nService;

	@Override
	public String getMessage(String key, Object... messageParams) throws NoSuchMessageException {
		return getMessage(key, messageParams, i18nService.getDefaultLocale());
	}

	@Override
	public String getMessage(String key, Locale locale, Object... messageParams) throws NoSuchMessageException {
		return messageSource.getMessage(key, messageParams, locale);
	}

}

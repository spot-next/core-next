package at.spot.core.infrastructure.spring.support;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractMessageSource;

import at.spot.core.data.model.internationalization.LocalizationKey;
import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.service.ModelService;

/**
 * Fetches spring message values from the database, instead of the property
 * files.
 */
// @Component("databaseMessageSource")
public class DatabaseMessageSource extends AbstractMessageSource {

	@Autowired
	protected ModelService modelService;

	@Override
	protected MessageFormat resolveCode(String key, Locale locale) {
		MessageFormat msg = null;

		Map<String, Object> keyParam = new HashMap<>();
		keyParam.put("key", key);

		List<LocalizationKey> translations = null;
		try {
			translations = modelService.get(LocalizationKey.class, keyParam);

		} catch (ModelNotFoundException e) {
		}

		if (translations != null && translations.size() > 0) {
			msg = new MessageFormat(translations.get(0).value, locale);
		}

		return msg;
	}

}

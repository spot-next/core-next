package at.spot.core.infrastructure.service.impl;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import javax.validation.MessageInterpolator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import at.spot.core.infrastructure.service.I18nService;
import at.spot.core.infrastructure.service.L10nService;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.itemtype.core.internationalization.LocalizationValue;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value = { "NP_NONNULL_RETURN_VIOLATION", "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE" })
public class DefaultL10nService extends AbstractService implements L10nService {

	protected MessageSource parentMessageSource;

	@Autowired
	protected I18nService i18nService;

	@Autowired
	protected ModelService modelService;

	@Override
	public String getMessage(final String key, final String defaultMessage, final Object... messageParams)
			throws NoSuchMessageException {

		return getMessage(key, defaultMessage, i18nService.getDefaultLocale(), messageParams);
	}

	@Override
	public String getMessage(final String key, final String defaultMessage, final Locale locale,
			final Object... messageParams) throws NoSuchMessageException {

		String message = null;

		try {
			message = getMessageFromStorage(key, defaultMessage, locale, messageParams);
		} catch (final NoSuchMessageException e) {
			message = getParentMessageSource().getMessage(key, messageParams, defaultMessage, locale);
		}

		// if no message has been found we return the key again
		if (StringUtils.isBlank(message)) {
			// message = String.format("<%s>", key);
			throw new NoSuchMessageException(key);
		}

		return message;
	}

	public String getMessageFromStorage(final String key, final String defaultMessage, final Locale locale,
			final Object... messageParams) throws NoSuchMessageException {

		LocalizationValue locResult = new LocalizationValue();
		locResult.setId(key);
		locResult.setLocale(locale);

		locResult = modelService.getByExample(locResult);

		if (locResult != null) {
			return locResult.getValue();
		} else {
			throw new NoSuchMessageException(key);
		}
	}

	/**
	 * {@link MessageSource} and {@link MessageInterpolator} implementations
	 */

	@Override
	public String getMessage(final String code, final Object[] args, final String defaultMessage, final Locale locale) {
		return getMessage(code, defaultMessage, locale, args);
	}

	@Override
	public String getMessage(final String code, final Object[] args, final Locale locale)
			throws NoSuchMessageException {

		return getMessage(code, null, locale, args);
	}

	@Override
	public String getMessage(final MessageSourceResolvable resolvable, final Locale locale)
			throws NoSuchMessageException {

		String message = null;

		if (resolvable != null && resolvable.getCodes() != null) {
			final Optional<String> firstCode = Arrays.asList(resolvable.getCodes()).stream().findFirst();

			if (firstCode.isPresent()) {
				message = getMessage(firstCode.get(), resolvable.getDefaultMessage(), locale,
						resolvable.getArguments());
			}
		}

		return message;
	}

	@Override
	public String interpolate(final String messageTemplate, final Context context) {
		return interpolate(messageTemplate, context, i18nService.getDefaultLocale());
	}

	@Override
	public String interpolate(final String messageTemplate, final Context context, final Locale locale) {
		String key = messageTemplate;

		// TODO: hacky, refactor into something more beautiful
		if (StringUtils.isNotBlank(key) && key.startsWith("{") && key.endsWith("}")) {
			key = key.substring(1, key.length() - 1);
		}

		final String message = getMessage(key, null, locale);

		return interpolateMessage(message, context);
	}

	protected String interpolateMessage(String message, final Context context) {
		for (final String key : context.getConstraintDescriptor().getAttributes().keySet()) {
			message = message.replace("{" + key + "}",
					context.getConstraintDescriptor().getAttributes().get(key).toString());
		}

		return message;
	}

	@Required
	public void setParentMessageSource(final MessageSource messageSource) {
		this.parentMessageSource = messageSource;
	}

	public MessageSource getParentMessageSource() {
		return parentMessageSource;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (parentMessageSource == null) {
			throw new IllegalStateException(
					"MessageSource was not injected, could not initialize " + this.getClass().getSimpleName());
		}

		// this.expressionFactory = ExpressionFactory.newInstance();
	}
}

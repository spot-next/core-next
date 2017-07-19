package at.spot.core.infrastructure.service;

import java.util.Locale;

import javax.validation.MessageInterpolator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

public interface L10nService extends MessageSource, MessageInterpolator, InitializingBean {

	/**
	 * Returns the message corresponding to the given key and replaces the
	 * passed message parameters. The default locale is used (from
	 * {@link I18nService}.
	 * 
	 * @param key
	 * @param messageParams
	 * @return
	 * @throws NoSuchMessageException
	 */
	String getMessage(String key, String defaultMessage, Object... messageParams) throws NoSuchMessageException;

	/**
	 * Returns the message corresponding to the given key and replaces the
	 * passed message parameters.
	 * 
	 * @param key
	 * @param messageParams
	 * @return
	 * @throws NoSuchMessageException
	 */
	String getMessage(String key, String defaultMessage, Locale locale, Object... messageParams)
			throws NoSuchMessageException;

}

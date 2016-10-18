package at.spot.core.infrastructure.service;

import java.util.Locale;

import org.springframework.context.NoSuchMessageException;

public interface L10nService {

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
	String getMessage(String key, Object... messageParams) throws NoSuchMessageException;

	/**
	 * Returns the message corresponding to the given key and replaces the
	 * passed message parameters.
	 * 
	 * @param key
	 * @param messageParams
	 * @return
	 * @throws NoSuchMessageException
	 */
	String getMessage(String key, Locale locale, Object... messageParams) throws NoSuchMessageException;

}

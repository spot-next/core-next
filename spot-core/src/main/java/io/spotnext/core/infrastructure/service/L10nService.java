package io.spotnext.core.infrastructure.service;

import java.util.Locale;

import javax.validation.MessageInterpolator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

/**
 * <p>L10nService interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface L10nService extends MessageSource, MessageInterpolator, InitializingBean {

	/**
	 * Returns the message corresponding to the given key and replaces the
	 * passed message parameters. The default locale is used (from
	 * {@link io.spotnext.core.infrastructure.service.I18nService}.
	 *
	 * @param key a {@link java.lang.String} object.
	 * @param messageParams a {@link java.lang.Object} object.
	 * @throws org.springframework.context.NoSuchMessageException
	 * @param defaultMessage a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	String getMessage(String key, String defaultMessage, Object... messageParams) throws NoSuchMessageException;

	/**
	 * Returns the message corresponding to the given key and replaces the
	 * passed message parameters.
	 *
	 * @param key a {@link java.lang.String} object.
	 * @param messageParams a {@link java.lang.Object} object.
	 * @throws org.springframework.context.NoSuchMessageException
	 * @param defaultMessage a {@link java.lang.String} object.
	 * @param locale a {@link java.util.Locale} object.
	 * @return a {@link java.lang.String} object.
	 */
	String getMessage(String key, String defaultMessage, Locale locale, Object... messageParams)
			throws NoSuchMessageException;

}

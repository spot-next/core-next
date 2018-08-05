package io.spotnext.core.support.util;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

public class ValidationUtil {

	public static void validateMinSize(String message, Collection<?> collection, int minSize) {
		if (collection == null || collection.size() < minSize) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * 
	 * @param message
	 *            the exception message. Use %s to output maxLength.
	 * @param text
	 *            the text which should be checked.
	 * @param maxLength
	 *            the max length of the text.
	 * @throws IllegalArgumentException
	 */
	public static void validateMaxLength(final String message, final String text, final int maxLength)
			throws IllegalArgumentException {

		if (StringUtils.length(text) > maxLength) {
			throw new IllegalArgumentException(String.format(message, maxLength));
		}
	}

	/**
	 * 
	 * @param message
	 *            the exception message. Use %s to output minLength.
	 * @param text
	 *            the text which should be checked.
	 * @param minLength
	 *            the min length of the text
	 * @throws IllegalArgumentException
	 */
	public static void validateMinLength(final String message, final String text, final int minLength)
			throws IllegalArgumentException {

		if (StringUtils.length(text) < minLength) {
			throw new IllegalArgumentException(String.format(message, minLength));
		}
	}

	/**
	 * 
	 * @param message
	 *            the exception message
	 * @param object
	 *            the object which should be checked.
	 * @throws IllegalArgumentException
	 */
	public static void validateNotNull(final String message, final Object object) throws IllegalArgumentException {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * 
	 * @param message
	 *            the exception message
	 * @param text
	 *            the text which should be checked.
	 * @throws IllegalArgumentException
	 */
	public static void validateNotEmpty(final String message, final String text) throws IllegalArgumentException {
		if (StringUtils.isBlank(text)) {
			throw new IllegalArgumentException(message);
		}
	}
}

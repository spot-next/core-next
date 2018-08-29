package io.spotnext.core.support.util;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>ValidationUtil class.</p>
 *
 * @since 1.0
 */
public class ValidationUtil {

	/**
	 * <p>validateMinSize.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param collection a {@link java.util.Collection} object.
	 * @param minSize a int.
	 */
	public static void validateMinSize(String message, Collection<?> collection, int minSize) {
		if (collection == null || collection.size() < minSize) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * <p>validateMaxLength.</p>
	 *
	 * @param message
	 *            the exception message. Use %s to output maxLength.
	 * @param text
	 *            the text which should be checked.
	 * @param maxLength
	 *            the max length of the text.
	 * @throws java.lang.IllegalArgumentException
	 */
	public static void validateMaxLength(final String message, final String text, final int maxLength)
			throws IllegalArgumentException {

		if (StringUtils.length(text) > maxLength) {
			throw new IllegalArgumentException(String.format(message, maxLength));
		}
	}

	/**
	 * <p>validateMinLength.</p>
	 *
	 * @param message
	 *            the exception message. Use %s to output minLength.
	 * @param text
	 *            the text which should be checked.
	 * @param minLength
	 *            the min length of the text
	 * @throws java.lang.IllegalArgumentException
	 */
	public static void validateMinLength(final String message, final String text, final int minLength)
			throws IllegalArgumentException {

		if (StringUtils.length(text) < minLength) {
			throw new IllegalArgumentException(String.format(message, minLength));
		}
	}

	/**
	 * <p>validateNotNull.</p>
	 *
	 * @param message
	 *            the exception message
	 * @param object
	 *            the object which should be checked.
	 * @throws java.lang.IllegalArgumentException
	 */
	public static void validateNotNull(final String message, final Object object) throws IllegalArgumentException {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * <p>validateNotEmpty.</p>
	 *
	 * @param message
	 *            the exception message
	 * @param text
	 *            the text which should be checked.
	 * @throws java.lang.IllegalArgumentException
	 */
	public static void validateNotEmpty(final String message, final String text) throws IllegalArgumentException {
		if (StringUtils.isBlank(text)) {
			throw new IllegalArgumentException(message);
		}
	}
	
	/**
	 * <p>validateEquals.</p>
	 *
	 * @param message
	 *            the exception message
	 * @param value
	 *            the boolean value to check.
	 * @throws java.lang.IllegalArgumentException
	 */
	public static void validateEquals(final String message, boolean value) throws IllegalArgumentException {
		if (!value) {
			throw new IllegalArgumentException(message);
		}
	}
}

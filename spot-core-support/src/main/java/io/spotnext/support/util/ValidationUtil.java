package io.spotnext.support.util;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * ValidationUtil class.
 * </p>
 *
 * @since 1.0
 * @author mojo2012
 * @version 1.0
 */
public class ValidationUtil {

	/**
	 * @param message    the error message shown if the collection size is less then
	 *                   the given min size
	 * @param collection a {@link java.util.Collection} object.
	 * @param minSize    of the collection
	 * @throws IllegalArgumentException if the collection size is below the given
	 *                                  min size
	 */
	public static void validateMinSize(String message, Collection<?> collection, int minSize)
			throws IllegalArgumentException {

		if (collection == null || collection.size() < minSize) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * @param message   the exception message. Use %s to output maxLength.
	 * @param text      the text which should be checked.
	 * @param maxLength the max length of the text.
	 * @throws IllegalArgumentException if the text exceeds the given max length
	 */
	public static void validateMaxLength(final String message, final String text, final int maxLength)
			throws IllegalArgumentException {

		if (StringUtils.length(text) > maxLength) {
			throw new IllegalArgumentException(String.format(message, maxLength));
		}
	}

	/**
	 * @param message   the exception message. Use %s to output minLength.
	 * @param text      the text which should be checked.
	 * @param minLength the min length of the text
	 * @throws IllegalArgumentException if the text length is below the given
	 *                                  minimum length
	 */
	public static void validateMinLength(final String message, final String text, final int minLength)
			throws IllegalArgumentException {

		if (StringUtils.length(text) < minLength) {
			throw new IllegalArgumentException(String.format(message, minLength));
		}
	}

	/**
	 * @param message the exception message
	 * @param object  the object which should be checked.
	 * @throws IllegalArgumentException if the object is null
	 */
	public static void validateNotNull(final String message, final Object object) throws IllegalArgumentException {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * @param message the exception message
	 * @param text    the text which should be checked.
	 * @throws IllegalArgumentException if the text is blank
	 */
	public static void validateNotEmpty(final String message, final String text) throws IllegalArgumentException {
		if (StringUtils.isBlank(text)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * @param message the exception message
	 * @param value   the boolean value to check.
	 * @throws IllegalArgumentException in case the value is not true
	 */
	public static void validateTrue(final String message, boolean value) throws IllegalArgumentException {
		if (!value) {
			throw new IllegalArgumentException(message);
		}
	}
}

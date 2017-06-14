package at.spot.spring.web.validation;

import org.apache.commons.lang3.StringUtils;

public class ValidationUtil {
	public static void validateMaxLength(final String message, final String text, final int maxLength)
			throws IllegalArgumentException {
		if (StringUtils.length(text) > maxLength) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void validateMinLength(final String message, final String text, final int minLength)
			throws IllegalArgumentException {
		if (StringUtils.length(text) < minLength) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void validateNotNull(final String message, final String text) throws IllegalArgumentException {
		if (text != null) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void validateNotEmpty(final String message, final String text) throws IllegalArgumentException {
		if (StringUtils.isNotBlank(text)) {
			throw new IllegalArgumentException(message);
		}
	}
}

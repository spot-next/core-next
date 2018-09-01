package io.spotnext.cms.exception;

/**
 * Exception that indicates that there was an error while rendering the
 * template.
 */
public class TemplateRenderException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message giving more information about the exception.
	 */
	public TemplateRenderException(final String message) {
		super(message);
	}

	/**
	 * @param message   giving more information about the exception.
	 * @param rootCause of the exception
	 */
	public TemplateRenderException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}
}

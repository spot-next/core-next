package io.spotnext.core.infrastructure.exception;

import java.util.Optional;

import io.spotnext.core.infrastructure.http.HttpResponse;

/**
 * <p>
 * AuthenticationException class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class AuthenticationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	final private Object response;

	/**
	 * <p>
	 * Constructor for AuthenticationException.
	 * </p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public AuthenticationException(final String message) {
		this(message, (HttpResponse) null);
	}

	/**
	 * <p>
	 * Constructor for AuthenticationException.
	 * </p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param the     response entity to return.
	 */
	public AuthenticationException(final String message, Object response) {
		super(message);
		this.response = response;
	}

	/**
	 * <p>
	 * Constructor for AuthenticationException.
	 * </p>
	 *
	 * @param message   a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public AuthenticationException(final String message, final Throwable rootCause) {
		super(message, rootCause);
		response = null;
	}

	public Optional<Object> getResponse() {
		return Optional.ofNullable(response);
	}

}

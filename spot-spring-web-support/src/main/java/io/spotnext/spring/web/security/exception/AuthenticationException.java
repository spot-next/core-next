package io.spotnext.spring.web.security.exception;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * <p>AuthenticationException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_SUPERCLASS")
public class AuthenticationException extends org.springframework.security.core.AuthenticationException {

	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for AuthenticationException.</p>
	 *
	 * @param msg a {@link java.lang.String} object.
	 */
	public AuthenticationException(final String msg) {
		super(msg);
	}

	/**
	 * <p>Constructor for AuthenticationException.</p>
	 *
	 * @param msg a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public AuthenticationException(final String msg, final Throwable rootCause) {
		super(msg, rootCause);
	}

}

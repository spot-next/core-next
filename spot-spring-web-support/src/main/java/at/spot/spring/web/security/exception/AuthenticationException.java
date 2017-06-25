package at.spot.spring.web.security.exception;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_SUPERCLASS")
public class AuthenticationException extends org.springframework.security.core.AuthenticationException {

	private static final long serialVersionUID = 1L;

	public AuthenticationException(final String msg) {
		super(msg);
	}

	public AuthenticationException(final String msg, final Throwable rootCause) {
		super(msg, rootCause);
	}

}

package at.spot.spring.web.security.exception;

public class AuthenticationException extends org.springframework.security.core.AuthenticationException {

	private static final long serialVersionUID = 1L;

	public AuthenticationException(final String msg) {
		super(msg);
	}

}

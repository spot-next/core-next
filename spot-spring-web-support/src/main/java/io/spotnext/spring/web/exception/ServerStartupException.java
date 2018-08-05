package io.spotnext.spring.web.exception;

public class ServerStartupException extends Exception {
	private static final long serialVersionUID = 1L;

	public ServerStartupException(String message) {
		super(message);
	}

	public ServerStartupException(String message, Throwable rootCause) {
		super(message, rootCause);
	}
}

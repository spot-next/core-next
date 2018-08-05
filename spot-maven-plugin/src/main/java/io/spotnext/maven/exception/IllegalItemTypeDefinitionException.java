package io.spotnext.maven.exception;

public class IllegalItemTypeDefinitionException extends Exception {
	private static final long serialVersionUID = 1L;

	public IllegalItemTypeDefinitionException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalItemTypeDefinitionException(String message) {
		super(message);
	}

	public IllegalItemTypeDefinitionException(Throwable cause) {
		super(cause);
	}
}

package at.spot.core.infrastructure.exception;

import at.spot.core.model.Item;

public class ModelNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public ModelNotFoundException(Class<? extends Item> type, long pk) {
		this(String.format("%s with pk=%s not found.", type.getName(), pk));
	}

	public ModelNotFoundException(final String message) {
		super(message);
	}

	public ModelNotFoundException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public ModelNotFoundException(final Throwable rootCause) {
		super(rootCause);
	}
}

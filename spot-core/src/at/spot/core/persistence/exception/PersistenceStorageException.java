package at.spot.core.persistence.exception;

public class PersistenceStorageException extends Exception {
	private static final long serialVersionUID = 1L;

	public PersistenceStorageException(final String message) {
		super(message);
	}

	public PersistenceStorageException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public PersistenceStorageException(final Throwable rootCause) {
		super(rootCause);
	}

	public PersistenceStorageException() {
		super();
	}
}

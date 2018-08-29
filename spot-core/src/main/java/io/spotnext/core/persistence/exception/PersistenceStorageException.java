package io.spotnext.core.persistence.exception;

/**
 * <p>PersistenceStorageException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class PersistenceStorageException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for PersistenceStorageException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public PersistenceStorageException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for PersistenceStorageException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public PersistenceStorageException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * <p>Constructor for PersistenceStorageException.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public PersistenceStorageException(final Throwable rootCause) {
		super(rootCause);
	}

	/**
	 * <p>Constructor for PersistenceStorageException.</p>
	 */
	public PersistenceStorageException() {
		super();
	}
}

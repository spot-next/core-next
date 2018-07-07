package at.spot.core.infrastructure.exception;

public class ImpexImportException extends ImportException {
	private static final long serialVersionUID = 1L;

	public ImpexImportException(final String message) {
		super(message);
	}

	public ImpexImportException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public ImpexImportException(final Throwable rootCause) {
		super(rootCause);
	}
}

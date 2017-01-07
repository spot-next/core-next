package at.spot.jfly.exception;

public class RemoteException extends Exception {
	private static final long serialVersionUID = 1L;

	public RemoteException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

}

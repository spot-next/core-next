package at.spot.core.remote.eception;

public class RemoteServiceInitException extends Exception {
	private static final long serialVersionUID = 1L;

	public RemoteServiceInitException(String message, Throwable rootCause) {
		super(message, rootCause);
	}

	public RemoteServiceInitException(String message) {
		super(message);
	}
}

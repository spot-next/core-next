package io.spotnext.core.infrastructure.scheduling.support;

public class CronJobAbortException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CronJobAbortException(String message) {
		super(message);
	}

	public CronJobAbortException(String message, Throwable rootCause) {
		super(message, rootCause);
	}
}

package io.spotnext.core.infrastructure.scheduling.support;

public class CronJobException extends Exception {
	private static final long serialVersionUID = 1L;

	public CronJobException(String message) {
		super(message);
	}

	public CronJobException(String message, Throwable rootCause) {
		super(message, rootCause);
	}
}

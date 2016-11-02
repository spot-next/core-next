package at.spot.core.infrastructure.service;

import at.spot.core.infrastructure.type.LogLevel;

public interface LoggingService {
	void debug(String message);

	void info(String message);

	void warn(String message);

	void error(String message);

	void exception(String message, Throwable exception);

	void log(LogLevel level, String message);

	void debug(String message, Class<?> callingClass);

	void info(String message, Class<?> callingClass);

	void warn(String message, Class<?> callingClass);

	void error(String message, Class<?> callingClass);

	void exception(String message, Throwable exception, Class<?> callingClass);

	void log(LogLevel level, String message, Class<?> callingClass);

	void log(LogLevel level, String message, Throwable exception, Class<?> callingClass);
}

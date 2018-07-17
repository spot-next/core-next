package at.spot.core.infrastructure.service;

import java.util.function.Supplier;

import at.spot.core.infrastructure.support.LogLevel;

public interface LoggingService {
	/**
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * 
	 * @param message
	 */
	void debug(String message);

	/**
	 * The passed supplier has to return the logging message. It will only be
	 * invoked, if the DEBUG mode is enabled. So you can do expensive calculations
	 * in there as well.
	 */
	void debug(Supplier<String> message);

	/**
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * 
	 * @param message
	 * @param callingClass
	 */
	void debug(String message, Class<?> callingClass);

	/**
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * 
	 * @param message
	 */
	void info(String message);

	/**
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * 
	 * @param message
	 * @param callingClass
	 */
	void info(String message, Class<?> callingClass);

	/**
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * 
	 * @param message
	 */
	void warn(String message);

	/**
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * 
	 * @param message
	 * @param callingClass
	 */
	void warn(String message, Class<?> callingClass);

	/**
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * 
	 * @param message
	 */
	void error(String message);

	/**
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * 
	 * @param message
	 * @param callingClass
	 */
	void error(String message, Class<?> callingClass);

	/**
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * 
	 * @param message
	 * @param exception
	 */
	void exception(String message, Throwable exception);

	/**
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * 
	 * @param message
	 * @param exception
	 * @param callingClass
	 */
	void exception(String message, Throwable exception, Class<?> callingClass);

	/**
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * 
	 * @param level
	 * @param message
	 * @param exception
	 * @param object
	 */
	void log(LogLevel level, String message, Throwable exception, Object object);

	/**
	 * Logs the given message, exception and info object to the configured log
	 * output.
	 * 
	 * @param level
	 *            the log level used
	 * @param message
	 *            the main log message
	 * @param exception
	 *            only logged if not null
	 * @param object
	 *            will be converted to json and attached to the log output, if not
	 *            null
	 * @param callingClass
	 *            the "origin" of the log statement
	 */
	void log(LogLevel level, String message, Throwable exception, Object object, Class<?> callingClass);
}

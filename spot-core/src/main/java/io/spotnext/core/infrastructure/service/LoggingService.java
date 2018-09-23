package io.spotnext.core.infrastructure.service;

import java.util.function.Supplier;

import io.spotnext.core.infrastructure.support.LogLevel;

/**
 * <p>LoggingService interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Deprecated
public interface LoggingService {
	/**
	 * <p>debug.</p>
	 *
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * @param message a {@link java.lang.String} object.
	 */
	void debug(String message);

	/**
	 * The passed supplier has to return the logging message. It will only be
	 * invoked, if the DEBUG mode is enabled. So you can do expensive calculations
	 * in there as well.
	 *
	 * @param message a {@link java.util.function.Supplier} object.
	 */
	void debug(Supplier<String> message);

	/**
	 * <p>debug.</p>
	 *
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * @param message a {@link java.lang.String} object.
	 * @param callingClass a {@link java.lang.Class} object.
	 */
	void debug(String message, Class<?> callingClass);

	/**
	 * <p>info.</p>
	 *
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * @param message a {@link java.lang.String} object.
	 */
	void info(String message);

	/**
	 * <p>info.</p>
	 *
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * @param message a {@link java.lang.String} object.
	 * @param callingClass a {@link java.lang.Class} object.
	 */
	void info(String message, Class<?> callingClass);

	/**
	 * <p>warn.</p>
	 *
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * @param message a {@link java.lang.String} object.
	 */
	void warn(String message);

	/**
	 * <p>warn.</p>
	 *
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * @param message a {@link java.lang.String} object.
	 * @param callingClass a {@link java.lang.Class} object.
	 */
	void warn(String message, Class<?> callingClass);

	/**
	 * <p>error.</p>
	 *
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * @param message a {@link java.lang.String} object.
	 */
	void error(String message);

	/**
	 * <p>error.</p>
	 *
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * @param message a {@link java.lang.String} object.
	 * @param callingClass a {@link java.lang.Class} object.
	 */
	void error(String message, Class<?> callingClass);

	/**
	 * <p>exception.</p>
	 *
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * @param message a {@link java.lang.String} object.
	 * @param exception a {@link java.lang.Throwable} object.
	 */
	void exception(String message, Throwable exception);

	/**
	 * <p>exception.</p>
	 *
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * @param message a {@link java.lang.String} object.
	 * @param exception a {@link java.lang.Throwable} object.
	 * @param callingClass a {@link java.lang.Class} object.
	 */
	void exception(String message, Throwable exception, Class<?> callingClass);

	/**
	 * <p>log.</p>
	 *
	 * @see #log(LogLevel, String, Throwable, Object, Class)
	 * @param level a {@link io.spotnext.infrastructure.support.LogLevel} object.
	 * @param message a {@link java.lang.String} object.
	 * @param exception a {@link java.lang.Throwable} object.
	 * @param object a {@link java.lang.Object} object.
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

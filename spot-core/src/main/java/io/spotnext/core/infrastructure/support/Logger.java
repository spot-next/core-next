package io.spotnext.core.infrastructure.support;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * This logging service maintains a list of {@link org.slf4j.Logger}s for each class that calls a log method. It logs to the {@link java.lang.System#console()}
 * as well as to the default log4j logger.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class Logger {
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE_TIME;

	private static final Map<Class<?>, org.slf4j.Logger> loggers = new HashMap<>();

	/** {@inheritDoc} */
	public static void debug(final String message) {
		log(LogLevel.DEBUG, message, null, null, getCallingClass());
	}

	/** {@inheritDoc} */
	public static void debug(Supplier<String> message) {
		final Class<?> callingClass = getCallingClass();
		final org.slf4j.Logger logger = getLoggerForClass(getCallingClass());

		if (logger.isDebugEnabled()) {
			log(LogLevel.DEBUG, message.get(), null, null, callingClass);
		}
	}

	/** {@inheritDoc} */
	public static void debug(final String message, final Class<?> callingClass) {
		log(LogLevel.DEBUG, message, null, null, callingClass);
	}

	/** {@inheritDoc} */
	public static void info(final String message) {
		log(LogLevel.INFO, message, null, null, getCallingClass());
	}

	/** {@inheritDoc} */
	public static void info(final String message, final Class<?> callingClass) {
		log(LogLevel.INFO, message, null, null, callingClass);
	}

	/** {@inheritDoc} */
	public static void warn(final String message) {
		log(LogLevel.WARN, message, null, null, getCallingClass());
	}

	/** {@inheritDoc} */
	public static void warn(final String message, final Class<?> callingClass) {
		log(LogLevel.WARN, message, null, null, callingClass);
	}

	/** {@inheritDoc} */
	public static void error(final String message) {
		log(LogLevel.ERROR, message, null, null, getCallingClass());
	}

	/** {@inheritDoc} */
	public static void error(final String message, final Class<?> callingClass) {
		log(LogLevel.ERROR, message, null, null, callingClass);
	}

	/** {@inheritDoc} */
	public static void exception(final String message, final Throwable exception) {
		log(LogLevel.FATAL, message, exception, null, getCallingClass());
	}

	/** {@inheritDoc} */
	public static void exception(final String message, final Throwable exception, final Class<?> callingClass) {
		log(LogLevel.FATAL, message, null, null, callingClass);
	}

	/*
	 * LOG METHODS
	 */

	/** {@inheritDoc} */
	public static void log(final LogLevel level, final String message, final Throwable exception, final Object object) {
		log(level, message, exception, object, getCallingClass());
	}

	/** {@inheritDoc} */
	public static void log(final LogLevel level, final String message, final Throwable exception, final Object object,
			final Class<?> callingClass) {

		String msg = message;

		if (exception != null) {
			exception.printStackTrace();
			msg += "\nException: " + ExceptionUtils.getStackTrace(exception);
		}

		String objectJson = "";

		if (object != null) {
			objectJson = "\nObject: " + toJson(object);
			objectJson = StringUtils.removeAll(objectJson, "\\s");
		}

		msg += objectJson;

		if (logToConsole()) {
			System.out.println(String.format("%s %s: %s", getTimeStamp(), level.toString(), msg));
		}

		final org.slf4j.Logger logger = getLoggerForClass(callingClass);

		if ((level == LogLevel.FATAL || level == LogLevel.ERROR) && logger.isErrorEnabled()) {
			logger.error(message);
		} else if (level == LogLevel.TRACE && logger.isTraceEnabled()) {
			logger.trace(message);
		} else if (level == LogLevel.WARN && logger.isWarnEnabled()) {
			logger.warn(message);
		} else if (level == LogLevel.INFO && logger.isInfoEnabled()) {
			logger.info(message);
		} else if (level == LogLevel.DEBUG && logger.isDebugEnabled()) {
			logger.debug(message);
		}
	}

	public static boolean isLogLevelEnabled(LogLevel logLevel) {
		switch (logLevel) {
		case DEBUG:
			return getLoggerForClass(getCallingClass()).isDebugEnabled();
		case INFO:
			return getLoggerForClass(getCallingClass()).isInfoEnabled();
		case ERROR:
			return getLoggerForClass(getCallingClass()).isErrorEnabled();
		case TRACE:
			return getLoggerForClass(getCallingClass()).isTraceEnabled();
		case WARN:
			return getLoggerForClass(getCallingClass()).isWarnEnabled();
		default:
			return true;
		}
	}

	/*
	 * Helper functions
	 */

	private static String toJson(Object object) {
		return "";
	}

	protected static boolean logToConsole() {
		return false;
	}

	protected static String getTimeStamp() {
		return LocalDateTime.now().format(DATE_FORMAT);
	}

	/**
	 * Returns the {@link Logger} object for the given class. It is also stored internally and created if it doesn't exist yet.
	 * 
	 * @param type
	 */
	protected static org.slf4j.Logger getLoggerForClass(final Class<?> type) {
		org.slf4j.Logger logger = loggers.get(type);

		if (logger == null) {
			logger = LoggerFactory.getLogger(type);
			loggers.put(type, logger);
		}

		return logger;
	}

	/**
	 * The calling class is parsed out of the stacktrace. This might not work in case any calls are intercepted (like with AspectJ).
	 */
	public static Class<?> getCallingClass() {
		Class<?> callingClass = null;

		final StackTraceElement[] stack = Thread.currentThread().getStackTrace();

		for (int i = 1; i < stack.length; i++) {
			final StackTraceElement o = stack[i];

			if (!StringUtils.equalsIgnoreCase(o.getClassName(), Logger.class.getName())) {
				try {
					callingClass = Class.forName(o.getClassName());
				} catch (final ClassNotFoundException e) {
					// ignore, as this should never happen
				}

				break;
			}
		}

		return callingClass;
	}
}

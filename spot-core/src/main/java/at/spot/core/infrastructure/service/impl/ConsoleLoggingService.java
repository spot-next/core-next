package at.spot.core.infrastructure.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.exception.SerializationException;
import at.spot.core.infrastructure.service.ConfigurationService;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.SerializationService;
import at.spot.core.infrastructure.support.LogLevel;

/**
 * This logging service maintains a list of {@link Logger}s for each class that
 * calls a log method. It logs to the {@link System#console()} as well as to the
 * default log4j logger.
 */
@Service
public class ConsoleLoggingService extends BeanAware implements LoggingService {

	private static final String CONFIG_KEY_LOG_TO_CONSOLE = "service.logging.sys.console";
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE_TIME;

	@Autowired
	protected ConfigurationService configurationService;

	@Autowired
	protected SerializationService serializationService;

	Map<Class<?>, Logger> loggers = new HashMap<>();

	@Override
	public void debug(final String message) {
		log(LogLevel.DEBUG, message, null, null, getCallingClass());
	}

	@Override
	public void debug(Supplier<String> message) {
		final Class<?> callingClass = getCallingClass();
		final Logger logger = getLoggerForClass(getCallingClass());

		if (logger.isDebugEnabled()) {
			log(LogLevel.DEBUG, message.get(), null, null, callingClass);
		}
	}

	@Override
	public void debug(final String message, final Class<?> callingClass) {
		log(LogLevel.DEBUG, message, null, null, callingClass);
	}

	@Override
	public void info(final String message) {
		log(LogLevel.INFO, message, null, null, getCallingClass());
	}

	@Override
	public void info(final String message, final Class<?> callingClass) {
		log(LogLevel.INFO, message, null, null, callingClass);
	}

	@Override
	public void warn(final String message) {
		log(LogLevel.WARN, message, null, null, getCallingClass());
	}

	@Override
	public void warn(final String message, final Class<?> callingClass) {
		log(LogLevel.WARN, message, null, null, callingClass);
	}

	@Override
	public void error(final String message) {
		log(LogLevel.ERROR, message, null, null, getCallingClass());
	}

	@Override
	public void error(final String message, final Class<?> callingClass) {
		log(LogLevel.ERROR, message, null, null, callingClass);
	}

	@Override
	public void exception(final String message, final Throwable exception) {
		log(LogLevel.FATAL, message, exception, null, getCallingClass());
	}

	@Override
	public void exception(final String message, final Throwable exception, final Class<?> callingClass) {
		log(LogLevel.FATAL, message, null, null, callingClass);
	}

	/*
	 * LOG METHODS
	 */

	@Override
	public void log(final LogLevel level, final String message, final Throwable exception, final Object object) {
		log(level, message, exception, object, getCallingClass());
	}

	@Override
	public void log(final LogLevel level, final String message, final Throwable exception, final Object object,
			final Class<?> callingClass) {

		String msg = message;

		if (exception != null) {
			exception.printStackTrace();
			msg += "\nException: " + ExceptionUtils.getStackTrace(exception);
		}

		String objectJson = "";

		if (object != null) {
			try {
				objectJson = "\nObject: " + serializationService.toJson(object);
				objectJson = StringUtils.removeAll(objectJson, "\\s");
			} catch (final SerializationException e) {
				objectJson = "Could not convert info object to JSON";
			}
		}

		msg += objectJson;

		if (logToConsole()) {
			System.out.println(String.format("%s %s: %s", getTimeStamp(), level.toString(), msg));
		}

		final Logger logger = getLoggerForClass(callingClass);

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

	/*
	 * Helper functions
	 */

	protected boolean logToConsole() {
		return configurationService.getBoolean(CONFIG_KEY_LOG_TO_CONSOLE, false);
	}

	protected String getTimeStamp() {
		return LocalDateTime.now().format(DATE_FORMAT);
	}

	/**
	 * Returns the {@link Logger} object for the given class. It is also stored
	 * internally and created if it doesn't exist yet.
	 * 
	 * @param type
	 */
	protected Logger getLoggerForClass(final Class<?> type) {
		Logger logger = loggers.get(type);

		if (logger == null) {
			logger = LoggerFactory.getLogger(type);
			loggers.put(type, logger);
		}

		return logger;
	}

	/**
	 * The calling class is parsed out of the stacktrace. This might not work in
	 * case any calls are intercepted (like with AspectJ).
	 */
	protected Class<?> getCallingClass() {
		Class<?> callingClass = null;

		final StackTraceElement[] stack = Thread.currentThread().getStackTrace();

		for (int i = 1; i < stack.length; i++) {
			final StackTraceElement o = stack[i];

			if (!StringUtils.equalsIgnoreCase(o.getClassName(), this.getClass().getName())) {
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

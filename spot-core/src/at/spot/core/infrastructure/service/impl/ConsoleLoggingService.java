package at.spot.core.infrastructure.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.service.ConfigurationService;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.type.LogLevel;

/**
 * This logging service maintains a list of {@link Logger}s for each class that
 * calls a log method. It logs to the {@link System#console()} as well as to the
 * default log4j logger.
 */
@Service
public class ConsoleLoggingService extends AbstractService implements LoggingService {

	private static final String CONFIG_KEY_LOG_TO_CONSOLE = "service.logging.sys.console";
	private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
	private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

	@Autowired
	protected ConfigurationService configurationService;

	Map<Class<?>, Logger> loggers = new HashMap<>();

	@Override
	public void debug(final String message, final Class<?> callingClass) {
		log(LogLevel.DEBUG, message, callingClass);
	}

	@Override
	public void info(final String message, final Class<?> callingClass) {
		log(LogLevel.INFO, message, callingClass);
	}

	@Override
	public void warn(final String message, final Class<?> callingClass) {
		log(LogLevel.WARN, message, callingClass);
	}

	@Override
	public void error(final String message, final Class<?> callingClass) {
		log(LogLevel.ERROR, message, callingClass);
	}

	@Override
	public void exception(final String message, final Throwable exception, final Class<?> callingClass) {
		log(LogLevel.FATAL, message, callingClass);
	}

	@Override
	public void log(final LogLevel level, final String message, final Throwable exception,
			final Class<?> callingClass) {

		String msg = message;

		if (exception != null) {
			exception.printStackTrace();
			msg += "\n" + ExceptionUtils.getStackTrace(exception);
		}

		if (logToConsole()) {
			System.out.println(String.format("%s %s: %s", getTimeStamp(), level.toString(), msg));
		}

		getLoggerForClass(callingClass).log(Level.toLevel(level.toString()), message);
	}

	protected boolean logToConsole() {
		return configurationService.getBoolean(CONFIG_KEY_LOG_TO_CONSOLE, true);
	}

	@Override
	public void log(final LogLevel level, final String message, final Class<?> callingClass) {
		log(level, message, null, callingClass);
	}

	@Override
	public void debug(final String message) {
		log(LogLevel.DEBUG, message, getCallingClass());
	}

	@Override
	public void info(final String message) {
		log(LogLevel.INFO, message, getCallingClass());
	}

	@Override
	public void warn(final String message) {
		log(LogLevel.WARN, message, getCallingClass());
	}

	@Override
	public void error(final String message) {
		log(LogLevel.ERROR, message, getCallingClass());
	}

	@Override
	public void exception(final String message, final Throwable exception) {
		log(LogLevel.FATAL, message, exception, getCallingClass());
	}

	@Override
	public void log(final LogLevel level, final String message) {
		log(level, message, getCallingClass());
	}

	/*
	 * Helper functions
	 */

	protected String getTimeStamp() {
		return sdf.format(new Date(System.currentTimeMillis()));
	}

	/**
	 * Returns the {@link Logger} object for the given class. It is also stored
	 * internally and created if it doesn't exist yet.
	 * 
	 * @param type
	 * @return
	 */
	protected Logger getLoggerForClass(final Class<?> type) {
		Logger logger = loggers.get(type);

		if (logger == null) {
			logger = LogManager.getLogger(type);
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

package at.spot.core.infrastructure.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.type.LogLevel;

/**
 * This logging service maintains a list of {@link Logger}s for each class that
 * calls a log method. It logs to the {@link System#console()} as well as to the
 * default log4j logger.
 */
@Service
public class ConsoleLoggingService extends AbstractService implements LoggingService {

	private static String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
	private static SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

	Map<Class<?>, Logger> loggers = new HashMap<>();

	@Override
	public void debug(String message, Class<?> callingClass) {
		log(LogLevel.DEBUG, message, callingClass);
	}

	@Override
	public void info(String message, Class<?> callingClass) {
		log(LogLevel.INFO, message, callingClass);
	}

	@Override
	public void warn(String message, Class<?> callingClass) {
		log(LogLevel.WARN, message, callingClass);
	}

	@Override
	public void error(String message, Class<?> callingClass) {
		log(LogLevel.ERROR, message, callingClass);
	}

	@Override
	public void exception(String message, Class<?> callingClass) {
		log(LogLevel.FATAL, message, callingClass);
	}

	@Override
	public void log(LogLevel level, String message, Class<?> callingClass) {
		System.out.println(String.format("%s %s: %s", getTimeStamp(), level.toString(), message));

		getLoggerForClass(callingClass).log(Level.toLevel(level.toString()), message);
	}

	@Override
	public void debug(String message) {
		log(LogLevel.DEBUG, message, getCallingClass());
	}

	@Override
	public void info(String message) {
		log(LogLevel.INFO, message, getCallingClass());
	}

	@Override
	public void warn(String message) {
		log(LogLevel.WARN, message, getCallingClass());
	}

	@Override
	public void error(String message) {
		log(LogLevel.ERROR, message, getCallingClass());
	}

	@Override
	public void exception(String message) {
		log(LogLevel.FATAL, message, getCallingClass());
	}

	@Override
	public void log(LogLevel level, String message) {
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
	protected Logger getLoggerForClass(Class<?> type) {
		Logger logger = loggers.get(type);

		if (logger == null) {
			logger = Logger.getLogger(type);
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

		StackTraceElement[] stack = Thread.currentThread().getStackTrace();

		for (int i = 1; i < stack.length; i++) {
			StackTraceElement o = stack[i];

			if (!StringUtils.equalsIgnoreCase(o.getClassName(), this.getClass().getName())) {
				try {
					callingClass = Class.forName(o.getClassName());
				} catch (ClassNotFoundException e) {
					// ignore, as this should never happen
				}
				break;
			}
		}

		return callingClass;
	}
}

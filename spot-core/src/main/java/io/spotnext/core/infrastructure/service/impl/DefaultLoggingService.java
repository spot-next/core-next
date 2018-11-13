package io.spotnext.core.infrastructure.service.impl;

import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.service.LoggingService;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.core.infrastructure.support.LogLevel;

@Service
public class DefaultLoggingService implements LoggingService {

	@Override
	public void debug(String message) {
		debug(message, Logger.getCallingClass());
	}

	@Override
	public void debug(Supplier<String> message) {
		Logger.debug(message);
	}

	@Override
	public void debug(String message, Class<?> callingClass) {
		Logger.debug(message, callingClass);
	}

	@Override
	public void info(String message) {
		info(message, Logger.getCallingClass());
	}

	@Override
	public void info(String message, Class<?> callingClass) {
		Logger.info(message, callingClass);
	}

	@Override
	public void warn(String message) {
		warn(message, Logger.getCallingClass());
	}

	@Override
	public void warn(String message, Class<?> callingClass) {
		Logger.warn(message, callingClass);
	}

	@Override
	public void error(String message) {
		error(message, Logger.getCallingClass());
	}

	@Override
	public void error(String message, Class<?> callingClass) {
		Logger.error(message, callingClass);
	}

	@Override
	public void exception(String message, Throwable exception) {
		exception(message, exception, Logger.getCallingClass());
	}

	@Override
	public void exception(String message, Throwable exception, Class<?> callingClass) {
		Logger.exception(message, exception, callingClass);
	}

	@Override
	public void log(LogLevel level, String message, Throwable exception, Object object) {
		log(level, message, exception, object, Logger.getCallingClass());
	}

	@Override
	public void log(LogLevel level, String message, Throwable exception, Object object, Class<?> callingClass) {
		Logger.log(level, message, exception, object, callingClass);
	}

}

package io.spotnext.core.infrastructure.service.impl;

import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.service.LoggingService;
import io.spotnext.core.infrastructure.support.Log;
import io.spotnext.core.infrastructure.support.LogLevel;

@Service
public class DefaultLoggingService implements LoggingService {

	@Override
	public void debug(String message) {
		debug(message, Log.getCallingClass());
	}

	@Override
	public void debug(Supplier<String> message) {
		Log.debug(message);
	}

	@Override
	public void debug(String message, Class<?> callingClass) {
		Log.debug(message, callingClass);
	}

	@Override
	public void info(String message) {
		info(message, Log.getCallingClass());
	}

	@Override
	public void info(String message, Class<?> callingClass) {
		Log.info(message, callingClass);
	}

	@Override
	public void warn(String message) {
		warn(message, Log.getCallingClass());
	}

	@Override
	public void warn(String message, Class<?> callingClass) {
		Log.warn(message, callingClass);
	}

	@Override
	public void error(String message) {
		error(message, Log.getCallingClass());
	}

	@Override
	public void error(String message, Class<?> callingClass) {
		Log.error(message, callingClass);
	}

	@Override
	public void exception(String message, Throwable exception) {
		exception(message, exception, Log.getCallingClass());
	}

	@Override
	public void exception(String message, Throwable exception, Class<?> callingClass) {
		Log.exception(message, exception, callingClass);
	}

	@Override
	public void log(LogLevel level, String message, Throwable exception, Object object) {
		log(level, message, exception, object, Log.getCallingClass());
	}

	@Override
	public void log(LogLevel level, String message, Throwable exception, Object object, Class<?> callingClass) {
		Log.log(level, message, exception, object, callingClass);
	}

}

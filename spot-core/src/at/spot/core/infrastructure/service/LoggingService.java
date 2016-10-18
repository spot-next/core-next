package at.spot.core.infrastructure.service;

import at.spot.core.infrastructure.type.LogLevel;

public interface LoggingService {
	void debug(String message);

	void info(String message);

	void warn(String message);

	void error(String message);

	void exception(String message);

	void log(LogLevel level, String message);

}

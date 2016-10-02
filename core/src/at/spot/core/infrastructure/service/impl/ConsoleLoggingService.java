package at.spot.core.infrastructure.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.service.LoggingService;

@Service
public class ConsoleLoggingService extends AbstractService implements LoggingService {

	private static String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
	private static SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

	@Override
	public void debug(String message) {
		log(LogLevel.DEBUG, message);
	}

	@Override
	public void info(String message) {
		log(LogLevel.INFO, message);
	}

	@Override
	public void warn(String message) {
		log(LogLevel.WARN, message);
	}

	@Override
	public void error(String message) {
		log(LogLevel.ERROR, message);
	}

	@Override
	public void exception(String message) {
		log(LogLevel.FATAL, message);
	}

	@Override
	public void log(LogLevel level, String message) {
		System.out.println(String.format("%s %s: %s", getTimeStamp(), level.toString(), message));
	}

	protected String getTimeStamp() {
		return sdf.format(new Date(System.currentTimeMillis()));
	}

}

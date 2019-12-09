package io.spotnext.support.util;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public interface Loggable {
	@SuppressFBWarnings("MS_OOI_PKGPROTECT")
	static Map<Class<?>, Logger> LOGGERS = new HashMap<>();

	default Logger log() {
		final var klass = this.getClass();
		var logger = LOGGERS.get(klass);

		if (logger == null) {
			logger = org.slf4j.LoggerFactory.getLogger(klass);
			LOGGERS.put(klass, logger);
		}

		return logger;
	}
}

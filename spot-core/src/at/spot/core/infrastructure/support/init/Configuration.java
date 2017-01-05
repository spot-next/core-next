package at.spot.core.infrastructure.support.init;

import java.util.List;
import java.util.Properties;

public interface Configuration {

	/**
	 * Return an ordered list of {@link Properties} application configuration
	 * objects.
	 * 
	 * @return
	 */
	List<Properties> getConfiguration();
}

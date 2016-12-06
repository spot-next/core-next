package at.spot.core.infrastructure.init;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import at.spot.core.support.util.PropertiesUtil;

public class ConfigurationHolder implements Configuration {
	protected static List<Properties> configProperties = new LinkedList<>();

	public void addConfigruation(final String configurationFile) {
		addConfigruation(PropertiesUtil.loadPropertiesFromFile(new File(configurationFile)));
	}

	public void addConfigruation(final File configurationFile) {
		addConfigruation(PropertiesUtil.loadPropertiesFromFile(configurationFile));
	}

	public void addConfigruation(final Properties configuration) {
		configProperties.add(0, configuration);
	}

	@Override
	public List<Properties> getConfiguration() {
		return configProperties;
	}
}

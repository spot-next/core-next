package at.spot.core.infrastructure.init;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import at.spot.core.support.util.PropertyUtil;

public class ConfigurationHolder {
	protected static Set<Properties> configProperties = new LinkedHashSet<>();

	public void addConfigruation(final String configurationFile) {
		addConfigruation(PropertyUtil.loadPropertiesFromClassPath(new File(configurationFile)));
	}

	public void addConfigruation(final File configurationFile) {
		addConfigruation(PropertyUtil.loadPropertiesFromClassPath(configurationFile));
	}

	public void addConfigruation(final Properties configuration) {
		configProperties.add(configuration);
	}

	public Set<Properties> getConfiguration() {
		return configProperties;
	}
}

package io.spotnext.core.infrastructure.support.init;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import io.spotnext.core.support.util.PropertiesUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("MS_MUTABLE_COLLECTION_PKGPROTECT")
public class ConfigurationHolder implements Configuration {
	protected static final List<Properties> configProperties = new LinkedList<>();

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

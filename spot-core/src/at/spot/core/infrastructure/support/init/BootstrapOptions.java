package at.spot.core.infrastructure.support.init;

public class BootstrapOptions {
	protected String appConfigFile;
	protected String springConfigFile;
	protected Class<? extends ModuleInit> initClass;

	public String getAppConfigFile() {
		return appConfigFile;
	}

	public BootstrapOptions setAppConfigFile(final String propertyFile) {
		this.appConfigFile = propertyFile;
		return this;
	}

	public Class<? extends ModuleInit> getInitClass() {
		return initClass;
	}

	public BootstrapOptions setInitClass(final Class<? extends ModuleInit> initClass) {
		this.initClass = initClass;
		return this;
	}

	public String getSpringConfigFile() {
		return springConfigFile;
	}

	public BootstrapOptions setSpringConfigFile(final String springConfigFile) {
		this.springConfigFile = springConfigFile;
		return this;
	}
}

package at.spot.core.infrastructure.init;

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

	public void setInitClass(final Class<? extends ModuleInit> initClass) {
		this.initClass = initClass;
	}

	public String getSpringConfigFile() {
		return springConfigFile;
	}

	public void setSpringConfigFile(final String springConfigFile) {
		this.springConfigFile = springConfigFile;
	}

}

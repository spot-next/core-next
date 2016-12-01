package at.spot.core.infrastructure.init;

public class BootstrapOptions {
	protected String propertyFile;
	protected Class<? extends ModuleInit> initClass;

	public String getPropertyFile() {
		return propertyFile;
	}

	public BootstrapOptions setPropertyFile(final String propertyFile) {
		this.propertyFile = propertyFile;

		return this;
	}

	public Class<? extends ModuleInit> getInitClass() {
		return initClass;
	}

	public void setInitClass(final Class<? extends ModuleInit> initClass) {
		this.initClass = initClass;
	}

}

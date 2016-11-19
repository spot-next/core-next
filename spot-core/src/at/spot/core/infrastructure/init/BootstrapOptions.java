package at.spot.core.infrastructure.init;

public class BootstrapOptions {
	protected String propertyFile;

	public String getPropertyFile() {
		return propertyFile;
	}

	public BootstrapOptions setPropertyFile(String propertyFile) {
		this.propertyFile = propertyFile;

		return this;
	}

}

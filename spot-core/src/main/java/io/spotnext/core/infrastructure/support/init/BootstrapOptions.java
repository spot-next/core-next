package io.spotnext.core.infrastructure.support.init;

public class BootstrapOptions {
	protected String appConfigFile;
	protected String springConfigFile;
	protected Class<? extends ModuleInit> initClass;
	protected boolean initializeTypeSystem = false;
	protected boolean updateTypeSystem = false;
	protected boolean cleanTypeSystem = false;
	protected boolean importInitialData = false;
	protected boolean importSampleData = false;

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

	public boolean isInitializeTypeSystem() {
		return initializeTypeSystem;
	}

	public BootstrapOptions setInitializeTypeSystem(final boolean initializeTypeSystem) {
		this.initializeTypeSystem = initializeTypeSystem;
		return this;
	}

	public boolean isUpdateTypeSystem() {
		return updateTypeSystem;
	}

	public BootstrapOptions setUpdateTypeSystem(final boolean updateTypeSystem) {
		this.updateTypeSystem = updateTypeSystem;
		return this;
	}

	public boolean isCleanTypeSystem() {
		return cleanTypeSystem;
	}

	public BootstrapOptions setCleanTypeSystem(final boolean cleanTypeSystem) {
		this.cleanTypeSystem = cleanTypeSystem;
		return this;
	}

	public boolean isImportInitialData() {
		return importInitialData;
	}

	public BootstrapOptions setImportInitialData(final boolean importInitialData) {
		this.importInitialData = importInitialData;
		return this;
	}

	public boolean isImportSampleData() {
		return importSampleData;
	}

	public BootstrapOptions setImportSampleData(final boolean importSampleData) {
		this.importSampleData = importSampleData;
		return this;
	}

}

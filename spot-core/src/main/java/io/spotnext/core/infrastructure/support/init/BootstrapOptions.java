package io.spotnext.core.infrastructure.support.init;

/**
 * <p>BootstrapOptions class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class BootstrapOptions {
	protected String appConfigFile;
	protected String springConfigFile;
	protected Class<? extends ModuleInit> initClass;
	protected boolean initializeTypeSystem = false;
	protected boolean updateTypeSystem = false;
	protected boolean cleanTypeSystem = false;
	protected boolean importInitialData = false;
	protected boolean importSampleData = false;

	/**
	 * <p>Getter for the field <code>appConfigFile</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getAppConfigFile() {
		return appConfigFile;
	}

	/**
	 * <p>Setter for the field <code>appConfigFile</code>.</p>
	 *
	 * @param propertyFile a {@link java.lang.String} object.
	 * @return a {@link io.spotnext.core.infrastructure.support.init.BootstrapOptions} object.
	 */
	public BootstrapOptions setAppConfigFile(final String propertyFile) {
		this.appConfigFile = propertyFile;
		return this;
	}

	/**
	 * <p>Getter for the field <code>initClass</code>.</p>
	 *
	 * @return a {@link java.lang.Class} object.
	 */
	public Class<? extends ModuleInit> getInitClass() {
		return initClass;
	}

	/**
	 * <p>Setter for the field <code>initClass</code>.</p>
	 *
	 * @param initClass a {@link java.lang.Class} object.
	 * @return a {@link io.spotnext.core.infrastructure.support.init.BootstrapOptions} object.
	 */
	public BootstrapOptions setInitClass(final Class<? extends ModuleInit> initClass) {
		this.initClass = initClass;
		return this;
	}

	/**
	 * <p>Getter for the field <code>springConfigFile</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getSpringConfigFile() {
		return springConfigFile;
	}

	/**
	 * <p>Setter for the field <code>springConfigFile</code>.</p>
	 *
	 * @param springConfigFile a {@link java.lang.String} object.
	 * @return a {@link io.spotnext.core.infrastructure.support.init.BootstrapOptions} object.
	 */
	public BootstrapOptions setSpringConfigFile(final String springConfigFile) {
		this.springConfigFile = springConfigFile;
		return this;
	}

	/**
	 * <p>isInitializeTypeSystem.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isInitializeTypeSystem() {
		return initializeTypeSystem;
	}

	/**
	 * <p>Setter for the field <code>initializeTypeSystem</code>.</p>
	 *
	 * @param initializeTypeSystem a boolean.
	 * @return a {@link io.spotnext.core.infrastructure.support.init.BootstrapOptions} object.
	 */
	public BootstrapOptions setInitializeTypeSystem(final boolean initializeTypeSystem) {
		this.initializeTypeSystem = initializeTypeSystem;
		return this;
	}

	/**
	 * <p>isUpdateTypeSystem.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isUpdateTypeSystem() {
		return updateTypeSystem;
	}

	/**
	 * <p>Setter for the field <code>updateTypeSystem</code>.</p>
	 *
	 * @param updateTypeSystem a boolean.
	 * @return a {@link io.spotnext.core.infrastructure.support.init.BootstrapOptions} object.
	 */
	public BootstrapOptions setUpdateTypeSystem(final boolean updateTypeSystem) {
		this.updateTypeSystem = updateTypeSystem;
		return this;
	}

	/**
	 * <p>isCleanTypeSystem.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isCleanTypeSystem() {
		return cleanTypeSystem;
	}

	/**
	 * <p>Setter for the field <code>cleanTypeSystem</code>.</p>
	 *
	 * @param cleanTypeSystem a boolean.
	 * @return a {@link io.spotnext.core.infrastructure.support.init.BootstrapOptions} object.
	 */
	public BootstrapOptions setCleanTypeSystem(final boolean cleanTypeSystem) {
		this.cleanTypeSystem = cleanTypeSystem;
		return this;
	}

	/**
	 * <p>isImportInitialData.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isImportInitialData() {
		return importInitialData;
	}

	/**
	 * <p>Setter for the field <code>importInitialData</code>.</p>
	 *
	 * @param importInitialData a boolean.
	 * @return a {@link io.spotnext.core.infrastructure.support.init.BootstrapOptions} object.
	 */
	public BootstrapOptions setImportInitialData(final boolean importInitialData) {
		this.importInitialData = importInitialData;
		return this;
	}

	/**
	 * <p>isImportSampleData.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isImportSampleData() {
		return importSampleData;
	}

	/**
	 * <p>Setter for the field <code>importSampleData</code>.</p>
	 *
	 * @param importSampleData a boolean.
	 * @return a {@link io.spotnext.core.infrastructure.support.init.BootstrapOptions} object.
	 */
	public BootstrapOptions setImportSampleData(final boolean importSampleData) {
		this.importSampleData = importSampleData;
		return this;
	}

}

package io.spotnext.core.infrastructure.support.init;

import java.util.List;

/**
 * <p>ModuleDefinition class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ModuleDefinition {
	private final String moduleName;
	private final List<String> modelPackagePaths;

	/**
	 * <p>Constructor for ModuleDefinition.</p>
	 *
	 * @param moduleName a {@link java.lang.String} object.
	 * @param modelPackagePaths a {@link java.util.List} object.
	 */
	public ModuleDefinition(final String moduleName, final List<String> modelPackagePaths) {
		this.moduleName = moduleName;
		this.modelPackagePaths = modelPackagePaths;
	}

	/**
	 * <p>Getter for the field <code>moduleName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getModuleName() {
		return moduleName;
	}

	/**
	 * <p>Getter for the field <code>modelPackagePaths</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<String> getModelPackagePaths() {
		return modelPackagePaths;
	}

}

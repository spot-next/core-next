package at.spot.core.infrastructure.support.init;

import java.util.List;

public class ModuleDefinition {
	private final String moduleName;
	private final List<String> modelPackagePaths;

	public ModuleDefinition(final String moduleName, final List<String> modelPackagePaths) {
		this.moduleName = moduleName;
		this.modelPackagePaths = modelPackagePaths;
	}

	public String getModuleName() {
		return moduleName;
	}

	public List<String> getModelPackagePaths() {
		return modelPackagePaths;
	}

}

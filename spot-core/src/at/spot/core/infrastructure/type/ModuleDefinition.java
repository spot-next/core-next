package at.spot.core.infrastructure.type;

public class ModuleDefinition {
	private String moduleName;
	private String modelPackagePath;

	public ModuleDefinition(String moduleName, String modelPackagePath) {
		this.moduleName = moduleName;
		this.modelPackagePath = modelPackagePath;
	}

	public String getModuleName() {
		return moduleName;
	}

	public String getModelPackagePath() {
		return modelPackagePath;
	}

}

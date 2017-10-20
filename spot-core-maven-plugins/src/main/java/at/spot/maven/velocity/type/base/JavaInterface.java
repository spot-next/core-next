package at.spot.maven.velocity.type.base;

import at.spot.maven.velocity.TemplateFile;
import at.spot.maven.velocity.type.AbstractComplexJavaType;

@TemplateFile("interface.vm")
public class JavaInterface extends AbstractComplexJavaType {
	private static final long serialVersionUID = 1L;

	public JavaInterface() {
	}

	public JavaInterface(String name, String packagePath) {
		super(name, packagePath);
	}

	public JavaInterface(Class<?> clazz) {
		super(clazz);
	}

}

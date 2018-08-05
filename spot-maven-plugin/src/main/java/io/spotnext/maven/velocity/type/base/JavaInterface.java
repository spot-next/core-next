package io.spotnext.maven.velocity.type.base;

import io.spotnext.maven.velocity.TemplateFile;
import io.spotnext.maven.velocity.type.AbstractComplexJavaType;

@TemplateFile("interface.vm")
public class JavaInterface extends AbstractComplexJavaType {
	private static final long serialVersionUID = 1L;

	public JavaInterface() {
	}

	public JavaInterface(final String name, final String packagePath) {
		super(name, packagePath);
	}

	public JavaInterface(final Class<?> clazz) {
		super(clazz);
	}

}

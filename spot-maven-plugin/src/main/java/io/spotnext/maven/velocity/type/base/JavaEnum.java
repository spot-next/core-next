package io.spotnext.maven.velocity.type.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.spotnext.maven.velocity.TemplateFile;
import io.spotnext.maven.velocity.type.parts.JavaEnumValue;

@TemplateFile("enum.vm")
public class JavaEnum extends JavaClass {
	private static final long serialVersionUID = 1L;

	protected final List<JavaEnumValue> values = new ArrayList<>();

	public JavaEnum(String name, String packagePath) {
		super(name, packagePath);
	}

	public List<JavaEnumValue> getValues() {
		return Collections.unmodifiableList(values);
	}

	public void addValue(JavaEnumValue value) {
		this.values.add(value);
	}

	@Override
	public void setSuperClass(JavaInterface superClass) {
		// ignore, as superclass is always Enum
	}
}

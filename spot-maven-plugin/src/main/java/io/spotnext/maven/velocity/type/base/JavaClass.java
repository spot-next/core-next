package io.spotnext.maven.velocity.type.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.spotnext.maven.velocity.TemplateFile;
import io.spotnext.maven.velocity.type.AbstractComplexJavaType;
import io.spotnext.maven.velocity.type.annotation.JavaAnnotation;
import io.spotnext.maven.velocity.type.parts.JavaField;

@TemplateFile("class.vm")
public class JavaClass extends AbstractComplexJavaType {
	private static final long serialVersionUID = 1L;

	protected final List<JavaField> fields = new ArrayList<>();
	protected boolean isAbstract;

	public JavaClass() {
	}

	public JavaClass(String name, String packagePath) {
		super(name, packagePath);
	}

	public JavaClass(Class<?> clazz) {
		super(clazz);
	}

	public List<JavaField> getFields() {
		return Collections.unmodifiableList(fields);
	}

	public void addField(JavaField field) {
		this.fields.add(field);

		if (field.getType().isComplexType()) {
			this.imports.add(field.getType().getFullyQualifiedName());
		}

		for (JavaAnnotation a : field.getAnnotations()) {
			this.imports.add(a.getType().getFullyQualifiedName());
		}
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	@Override
	public Set<String> getImports() {
		final Set<String> allImports = super.getImports();

		allImports.addAll(fields.stream().flatMap(f -> f.getImports().stream()).collect(Collectors.toSet()));

		return allImports;
	}

	public void addImport(String importType) {
		getImports().add(importType);
	}

	public void addImport(Class<?> importType) {
		getImports().add(importType.getName());
	}
}

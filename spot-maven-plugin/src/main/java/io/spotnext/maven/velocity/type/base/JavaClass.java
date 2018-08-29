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

/**
 * <p>JavaClass class.</p>
 *
 * @since 1.0
 */
@TemplateFile("class.vm")
public class JavaClass extends AbstractComplexJavaType {
	private static final long serialVersionUID = 1L;

	protected final List<JavaField> fields = new ArrayList<>();
	protected boolean isAbstract;

	/**
	 * <p>Constructor for JavaClass.</p>
	 */
	public JavaClass() {
	}

	/**
	 * <p>Constructor for JavaClass.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param packagePath a {@link java.lang.String} object.
	 */
	public JavaClass(String name, String packagePath) {
		super(name, packagePath);
	}

	/**
	 * <p>Constructor for JavaClass.</p>
	 *
	 * @param clazz a {@link java.lang.Class} object.
	 */
	public JavaClass(Class<?> clazz) {
		super(clazz);
	}

	/**
	 * <p>Getter for the field <code>fields</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<JavaField> getFields() {
		return Collections.unmodifiableList(fields);
	}

	/**
	 * <p>addField.</p>
	 *
	 * @param field a {@link io.spotnext.maven.velocity.type.parts.JavaField} object.
	 */
	public void addField(JavaField field) {
		this.fields.add(field);

		if (field.getType().isComplexType()) {
			this.imports.add(field.getType().getFullyQualifiedName());
		}

		for (JavaAnnotation a : field.getAnnotations()) {
			this.imports.add(a.getType().getFullyQualifiedName());
		}
	}

	/**
	 * <p>setAbstract.</p>
	 *
	 * @param isAbstract a boolean.
	 */
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	/**
	 * <p>isAbstract.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isAbstract() {
		return isAbstract;
	}

	/** {@inheritDoc} */
	@Override
	public Set<String> getImports() {
		final Set<String> allImports = super.getImports();

		allImports.addAll(fields.stream().flatMap(f -> f.getImports().stream()).collect(Collectors.toSet()));

		return allImports;
	}

	/**
	 * <p>addImport.</p>
	 *
	 * @param importType a {@link java.lang.String} object.
	 */
	public void addImport(String importType) {
		getImports().add(importType);
	}

	/**
	 * <p>addImport.</p>
	 *
	 * @param importType a {@link java.lang.Class} object.
	 */
	public void addImport(Class<?> importType) {
		getImports().add(importType.getName());
	}
}

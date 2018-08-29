package io.spotnext.maven.velocity.type.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.spotnext.maven.velocity.TemplateFile;
import io.spotnext.maven.velocity.type.parts.JavaEnumValue;

/**
 * <p>JavaEnum class.</p>
 *
 * @since 1.0
 */
@TemplateFile("enum.vm")
public class JavaEnum extends JavaClass {
	private static final long serialVersionUID = 1L;

	protected final List<JavaEnumValue> values = new ArrayList<>();

	/**
	 * <p>Constructor for JavaEnum.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param packagePath a {@link java.lang.String} object.
	 */
	public JavaEnum(String name, String packagePath) {
		super(name, packagePath);
	}

	/**
	 * <p>Getter for the field <code>values</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<JavaEnumValue> getValues() {
		return Collections.unmodifiableList(values);
	}

	/**
	 * <p>addValue.</p>
	 *
	 * @param value a {@link io.spotnext.maven.velocity.type.parts.JavaEnumValue} object.
	 */
	public void addValue(JavaEnumValue value) {
		this.values.add(value);
	}

	/** {@inheritDoc} */
	@Override
	public void setSuperClass(JavaInterface superClass) {
		// ignore, as superclass is always Enum
	}
}

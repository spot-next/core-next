package io.spotnext.maven.velocity.type.annotation;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.spotnext.maven.velocity.type.AbstractObject;
import io.spotnext.maven.velocity.type.parts.JavaExpression;
import io.spotnext.maven.velocity.type.parts.JavaMemberType;

/**
 * <p>JavaAnnotation class.</p>
 *
 * @since 1.0
 */
public class JavaAnnotation extends AbstractObject {
	private static final long serialVersionUID = 1L;

	protected JavaMemberType type;
	protected Map<String, JavaExpression> parameters = new HashMap<>();

	/**
	 * <p>Constructor for JavaAnnotation.</p>
	 *
	 * @param type a {@link java.lang.Class} object.
	 */
	public JavaAnnotation(final Class<? extends Annotation> type) {
		super(type.getSimpleName());
		this.type = new JavaMemberType(type);
		this.imports.add(type.getName());
	}

	/**
	 * <p>Constructor for JavaAnnotation.</p>
	 *
	 * @param type a {@link io.spotnext.maven.velocity.type.parts.JavaMemberType} object.
	 */
	public JavaAnnotation(final JavaMemberType type) {
		super(type.getName());
		this.type = type;
	}

	/**
	 * <p>Constructor for JavaAnnotation.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param packagePath a {@link java.lang.String} object.
	 */
	public JavaAnnotation(final String name, final String packagePath) {
		super(name);
		this.type = new JavaMemberType(name, packagePath);
	}

	/**
	 * <p>Getter for the field <code>type</code>.</p>
	 *
	 * @return a {@link io.spotnext.maven.velocity.type.parts.JavaMemberType} object.
	 */
	public JavaMemberType getType() {
		return type;
	}

	/**
	 * <p>Getter for the field <code>parameters</code>.</p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public Map<String, JavaExpression> getParameters() {
		return parameters;
	}

	/**
	 * <p>Setter for the field <code>parameters</code>.</p>
	 *
	 * @param parameters a {@link java.util.Map} object.
	 */
	public void setParameters(final Map<String, JavaExpression> parameters) {
		this.parameters = parameters;
	}

	/**
	 * <p>addParameter.</p>
	 *
	 * @param key a {@link java.lang.String} object.
	 * @param value a {@link java.lang.Class} object.
	 */
	public void addParameter(final String key, final Class<?> value) {
		addParameter(key, value.getName(), JavaValueType.CLASS);
	}

	/**
	 * <p>addParameter.</p>
	 *
	 * @param key a {@link java.lang.String} object.
	 * @param value a {@link java.lang.Object} object.
	 * @param valueType a {@link io.spotnext.maven.velocity.type.annotation.JavaValueType} object.
	 */
	public void addParameter(final String key, final Object value, final JavaValueType valueType) {
		final JavaExpression val = new JavaExpression(value, valueType);
		this.parameters.put(key, val);
	}

	/** {@inheritDoc} */
	@Override
	public Set<String> getImports() {
		final Set<String> allImports = super.getImports();

		allImports.addAll(type.getImports());

		for (final JavaExpression val : parameters.values()) {
			allImports.add(val.getValue().getClass().getName());
		}

		return allImports;
	}
}

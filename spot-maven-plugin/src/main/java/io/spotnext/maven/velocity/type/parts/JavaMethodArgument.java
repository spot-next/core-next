package io.spotnext.maven.velocity.type.parts;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.spotnext.maven.velocity.type.AbstractObject;
import io.spotnext.maven.velocity.type.annotation.JavaAnnotation;

/**
 * <p>
 * JavaMethodArgument class.
 * </p>
 *
 * @since 1.0
 */
public class JavaMethodArgument extends AbstractObject {
	private static final long serialVersionUID = 1L;

	protected final Set<JavaAnnotation> annotations = new HashSet<>();
	protected JavaMemberType type;

	/**
	 * <p>
	 * Constructor for JavaMethodArgument.
	 * </p>
	 *
	 * @param type a {@link io.spotnext.maven.velocity.type.parts.JavaMemberType} object.
	 * @param name a {@link java.lang.String} object.
	 */
	public JavaMethodArgument(JavaMemberType type, String name) {
		this.name = name;
		this.type = type;
	}

	/**
	 * <p>
	 * Getter for the field <code>type</code>.
	 * </p>
	 *
	 * @return a {@link io.spotnext.maven.velocity.type.parts.JavaMemberType} object.
	 */
	public JavaMemberType getType() {
		return type;
	}

	/**
	 * <p>
	 * Setter for the field <code>type</code>.
	 * </p>
	 *
	 * @param type a {@link io.spotnext.maven.velocity.type.parts.JavaMemberType} object.
	 */
	public void setType(JavaMemberType type) {
		this.type = type;
	}

	/**
	 * <p>
	 * addAnnotation.
	 * </p>
	 *
	 * @param annotation a {@link io.spotnext.maven.velocity.type.annotation.JavaAnnotation} object.
	 */
	public void addAnnotation(JavaAnnotation annotation) {
		getImports().add(annotation.getType().getFullyQualifiedName());
		this.annotations.add(annotation);
	}

	/**
	 * <p>
	 * Getter for the field <code>annotations</code>.
	 * </p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<JavaAnnotation> getAnnotations() {
		return Collections.unmodifiableSet(annotations);
	}

	/** {@inheritDoc} */
	@Override
	public Set<String> getImports() {
		final Set<String> allImports = super.getImports();
		allImports.addAll(type.getImports());

		return allImports;
	}
}

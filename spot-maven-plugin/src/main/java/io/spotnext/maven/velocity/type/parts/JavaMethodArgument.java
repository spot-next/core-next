package io.spotnext.maven.velocity.type.parts;

import java.util.Set;

import io.spotnext.maven.velocity.type.AbstractObject;

/**
 * <p>JavaMethodArgument class.</p>
 *
 * @since 1.0
 */
public class JavaMethodArgument extends AbstractObject {
	private static final long serialVersionUID = 1L;

	protected JavaMemberType type;

	/**
	 * <p>Constructor for JavaMethodArgument.</p>
	 *
	 * @param type a {@link io.spotnext.maven.velocity.type.parts.JavaMemberType} object.
	 * @param name a {@link java.lang.String} object.
	 */
	public JavaMethodArgument(JavaMemberType type, String name) {
		this.name = name;
		this.type = type;
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
	 * <p>Setter for the field <code>type</code>.</p>
	 *
	 * @param type a {@link io.spotnext.maven.velocity.type.parts.JavaMemberType} object.
	 */
	public void setType(JavaMemberType type) {
		this.type = type;
	}

	/** {@inheritDoc} */
	@Override
	public Set<String> getImports() {
		final Set<String> allImports = super.getImports();
		allImports.addAll(type.getImports());

		return allImports;
	}
}

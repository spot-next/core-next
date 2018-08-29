package io.spotnext.maven.velocity.type.parts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.spotnext.maven.velocity.JavaMemberModifier;
import io.spotnext.maven.velocity.type.AbstractJavaObject;

/**
 * <p>Abstract JavaMember class.</p>
 *
 * @since 1.0
 */
public abstract class JavaMember extends AbstractJavaObject {
	private static final long serialVersionUID = 1L;

	protected JavaMemberType type;
	protected final List<JavaMemberModifier> modifiers = new ArrayList<>();

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

		if (type != null) {
			allImports.addAll(type.getImports());
		}

		return allImports;
	}

	/**
	 * <p>addModifier.</p>
	 *
	 * @param modifier a {@link io.spotnext.maven.velocity.JavaMemberModifier} object.
	 */
	public void addModifier(JavaMemberModifier modifier) {
		this.modifiers.add(modifier);
	}

	/**
	 * <p>Getter for the field <code>modifiers</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<JavaMemberModifier> getModifiers() {
		return modifiers;
	}

}

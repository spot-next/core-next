package io.spotnext.maven.velocity.type.parts;

import java.io.Serializable;

/**
 * <p>JavaGenericTypeArgument class.</p>
 *
 * @since 1.0
 */
public class JavaGenericTypeArgument implements Serializable {
	private static final long serialVersionUID = 1L;

	protected JavaMemberType type;
	protected boolean isWildCard = false;

	/**
	 * <p>Constructor for JavaGenericTypeArgument.</p>
	 *
	 * @param type a {@link io.spotnext.maven.velocity.type.parts.JavaMemberType} object.
	 * @param isWildCard a boolean.
	 */
	public JavaGenericTypeArgument(JavaMemberType type, boolean isWildCard) {
		this.isWildCard = isWildCard;
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

	/**
	 * <p>isWildCard.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isWildCard() {
		return isWildCard;
	}

	/**
	 * <p>setWildCard.</p>
	 *
	 * @param isWildCard a boolean.
	 */
	public void setWildCard(boolean isWildCard) {
		this.isWildCard = isWildCard;
	}
}

package io.spotnext.maven.velocity.type.parts;

/**
 * <p>JavaField class.</p>
 *
 * @since 1.0
 */
public class JavaField extends JavaMember {
	private static final long serialVersionUID = 1L;

	protected JavaExpression assignement;

	/**
	 * <p>Getter for the field <code>assignement</code>.</p>
	 *
	 * @return a {@link io.spotnext.maven.velocity.type.parts.JavaExpression} object.
	 */
	public JavaExpression getAssignement() {
		return assignement;
	}

	/**
	 * <p>Setter for the field <code>assignement</code>.</p>
	 *
	 * @param assignement a {@link io.spotnext.maven.velocity.type.parts.JavaExpression} object.
	 */
	public void setAssignement(JavaExpression assignement) {
		this.assignement = assignement;
	}
}

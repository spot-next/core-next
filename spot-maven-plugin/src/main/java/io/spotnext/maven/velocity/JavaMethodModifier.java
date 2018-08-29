package io.spotnext.maven.velocity;

/**
 * <p>JavaMethodModifier class.</p>
 *
 * @since 1.0
 */
public enum JavaMethodModifier {
	NONE(""), ABSTRACT("abstract"), VOLATILE("volatile"), SYNCHRONIZED("synchronized");

	private String code;

	private JavaMethodModifier(String code) {
		this.code = code;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.code;
	}

	/**
	 * <p>Getter for the field <code>code</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getCode() {
		return this.code;
	}
}

package io.spotnext.maven.velocity;

/**
 * <p>JavaMemberModifier class.</p>
 *
 * @since 1.0
 */
public enum JavaMemberModifier {
	NONE(""), STATIC("static"), FINAL("final");

	private final String code;

	private JavaMemberModifier(String code) {
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
		return code;
	}
}

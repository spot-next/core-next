package io.spotnext.maven.velocity;

/**
 * <p>Visibility class.</p>
 *
 * @since 1.0
 */
public enum Visibility {
	PUBLIC("public"), PACKAGE_PRIVATE(""), PROTECTED("protected"), PRIVATE("private");

	private final String code;

	private Visibility(String code) {
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

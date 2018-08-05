package io.spotnext.maven.velocity;

public enum Visibility {
	PUBLIC("public"), PACKAGE_PRIVATE(""), PROTECTED("protected"), PRIVATE("private");

	private final String code;

	private Visibility(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return this.code;
	}

	public String getCode() {
		return code;
	}

}

package io.spotnext.maven.velocity;

public enum JavaMemberModifier {
	NONE(""), STATIC("static"), FINAL("final");

	private final String code;

	private JavaMemberModifier(String code) {
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

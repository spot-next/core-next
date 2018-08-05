package io.spotnext.maven.velocity;

public enum JavaMethodModifier {
	NONE(""), ABSTRACT("abstract"), VOLATILE("volatile"), SYNCHRONIZED("synchronized");

	private String code;

	private JavaMethodModifier(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return this.code;
	}

	public String getCode() {
		return this.code;
	}
}

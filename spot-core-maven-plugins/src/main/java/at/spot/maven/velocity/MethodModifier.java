package at.spot.maven.velocity;

public enum MethodModifier {
	NONE(""), ABSTRACT("abstract"), VOLATILE("volatile"), SYNCHRONIZED("synchronized"), FINAL("final");

	private String token;

	private MethodModifier(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return this.token;
	}
}

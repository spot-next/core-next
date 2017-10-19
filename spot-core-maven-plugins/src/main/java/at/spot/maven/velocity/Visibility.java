package at.spot.maven.velocity;

public enum Visibility {
	PUBLIC("public"), PACKAGE_PRIVATE("private"), PROTECTED("protected"), PRIVATE("private");

	private String token;

	private Visibility(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return this.token;
	}
}

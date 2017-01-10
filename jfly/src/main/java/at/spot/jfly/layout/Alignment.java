package at.spot.jfly.layout;

public enum Alignment {
	Top("top"), Bottom("bottom"), Right("right"), Left("left"), Center("center");

	private String internal;

	private Alignment(final String internal) {
		this.internal = internal;
	}

	@Override
	public String toString() {
		return this.internal;
	}
}

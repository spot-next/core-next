package at.spot.jfly.layout;

public enum Direction {
	Up("up"), Down("down"), Right("right"), Left("left");

	private String internal;

	private Direction(final String internal) {
		this.internal = internal;
	}

	@Override
	public String toString() {
		return this.internal;
	}
}

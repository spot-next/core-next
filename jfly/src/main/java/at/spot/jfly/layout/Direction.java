package at.spot.jfly.layout;

import at.spot.jfly.style.Style;

public enum Direction implements Style {
	Up("up"), Down("down"), Right("right"), Left("left");

	private String internal;

	private Direction(final String internal) {
		this.internal = internal;
	}

	@Override
	public String internalName() {
		return this.internal;
	}
}

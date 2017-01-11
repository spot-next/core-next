package at.spot.jfly.layout;

import at.spot.jfly.style.Style;

public enum Alignment implements Style {
	Top("top"), Bottom("bottom"), Right("right"), Left("left"), Center("center");

	private String internal;

	private Alignment(final String internal) {
		this.internal = internal;
	}

	@Override
	public String internalName() {
		return this.internal;
	}
}

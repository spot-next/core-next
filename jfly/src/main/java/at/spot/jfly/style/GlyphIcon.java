package at.spot.jfly.style;

public enum GlyphIcon {
	Ok("glyphicon-ok");

	private String internal;

	private GlyphIcon(final String internal) {
		this.internal = internal;
	}

	@Override
	public String toString() {
		return this.internal;
	}
}

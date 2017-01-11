package at.spot.jfly.style;

public enum GlyphIcon implements Style {
	Ok("glyphicon-ok");

	private String internal;

	private GlyphIcon(final String internal) {
		this.internal = internal;
	}

	@Override
	public String internalName() {
		return this.internal;
	}
}

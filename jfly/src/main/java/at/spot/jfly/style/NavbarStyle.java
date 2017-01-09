package at.spot.jfly.style;

/**
 * All general predefined styles.
 */
public enum NavbarStyle implements Style {
	Default("navbar-default"), Inverse("navbar-inverse");

	private String styleClass;

	private NavbarStyle(final String internal) {
		this.styleClass = internal;
	}

	@Override
	public String toString() {
		return this.styleClass;
	}
}

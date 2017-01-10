package at.spot.jfly.style;

/**
 * NavBar related styles.
 */
public enum NavbarStyle implements Style {
	Default("default"),
	Inverse("navbar inverse"),
	NavBarHeader("navbar-header"),
	NavBarContent("nav navbar-nav"),
	NavBarBrand("navbar-brand");

	private String styleClass;

	private NavbarStyle(final String internal) {
		this.styleClass = internal;
	}

	@Override
	public String toString() {
		return this.styleClass;
	}
}

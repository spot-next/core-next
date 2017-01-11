package at.spot.jfly.style;

/**
 * NavBar related styles.
 */
public enum NavbarStyle implements Style {
	Default("default"),
	Inverse("inverse"),
	Primary("primary"),
	Success("success"),
	Info("info"),
	Warning("warning"),
	Danger("danger"),
	NavBarHeader("navbar-header"),
	NavBarContent("nav navbar-nav"),
	NavBarHeaderBrand("navbar-brand");

	private String styleClass;

	private NavbarStyle(final String internal) {
		this.styleClass = internal;
	}

	@Override
	public String internalName() {
		return this.styleClass;
	}
}

package at.spot.jfly.style;

/**
 * All general predefined styles.
 */
public enum ComponentStyle implements Style {
	Button("btn"), Label("label"), Badge("badge"), NavBar("navbar"), NavBarHeader("navbar-header"), NavBarContent(
			"nav navbar-nav"), NavBarBrand("navbar-brand");

	private String styleClass;

	private ComponentStyle(final String internal) {
		this.styleClass = internal;
	}

	@Override
	public String toString() {
		return this.styleClass;
	}
}

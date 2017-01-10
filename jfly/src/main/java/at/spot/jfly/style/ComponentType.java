package at.spot.jfly.style;

/**
 * All general predefined styles.
 */
public enum ComponentType implements Style {
	Button("button"), Label("label"), Badge("badge"), NavBarDefault("default"), NavBarInverse("inverse");

	private String styleClass;

	private ComponentType(final String internal) {
		this.styleClass = internal;
	}

	@Override
	public String toString() {
		return this.styleClass;
	}
}

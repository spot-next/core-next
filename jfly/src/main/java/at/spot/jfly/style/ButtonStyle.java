package at.spot.jfly.style;

/**
 * All possible predefined styles for buttons.
 */
public enum ButtonStyle implements Style {
	None("btn"),
	Default("btn btn-default"),
	Primary("btn btn-primary"),
	Success("btn btn-success"),
	Info("btn btn-info"),
	Warning("btn btn-warning"),
	Danger("btn btn-danger"),
	Link("btn btn-link");

	private String styleClass;

	private ButtonStyle(final String styleClass) {
		this.styleClass = styleClass;
	}

	@Override
	public String toString() {
		return this.styleClass;
	}
}

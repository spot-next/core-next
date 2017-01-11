package at.spot.jfly.style;

/**
 * All possible predefined styles for labels.
 */
public enum LabelStyle implements Style {
	None("label"),
	Default("label label-default"),
	Primary("label label-primary"),
	Success("label label-success"),
	Info("label label-info"),
	Warning("label label-warning"),
	Danger("label label-danger"),;

	private String styleClass;

	private LabelStyle(final String styleClass) {
		this.styleClass = styleClass;
	}

	@Override
	public String internalName() {
		return this.styleClass;
	}
}

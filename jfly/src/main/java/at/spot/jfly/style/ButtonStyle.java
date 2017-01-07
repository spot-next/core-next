package at.spot.jfly.style;

public enum ButtonStyle {
	Default("btn-default"), Primary("btn-primary"), Success("btn-success"), Info("btn-info"), Warning(
			"btn-warning"), Danger("btn-danger"), Link("btn-link"),;

	private String styleClass;

	private ButtonStyle(final String styleClass) {
		this.styleClass = styleClass;
	}

	@Override
	public String toString() {
		return this.styleClass;
	}
}

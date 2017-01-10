package at.spot.jfly.style;

/**
 * All possible predefined styles for labels.
 */
public enum BadgeStyle implements Style {
	None("badge");

	private String styleClass;

	private BadgeStyle(final String styleClass) {
		this.styleClass = styleClass;
	}

	@Override
	public String toString() {
		return this.styleClass;
	}
}

package at.spot.jfly.ui;

import at.spot.jfly.style.LabelStyle;

/**
 * Implements a bootstrap button:<br>
 * <br>
 * <button type="button" class="btn btn-default">Default</button>
 */
public class Label extends Badge {

	private LabelStyle style;

	public Label(final String text) {
		super(text);
		setBaseStyleClass("label");
	}

	public Label style(final LabelStyle style) {
		this.style = style;
		controller().invokeComponentManipulation(this, "addClass", style.toString());
		return this;
	}

	@Override
	protected String getStyleClasses() {
		final String classes = super.getStyleClasses() + style.toString();

		return classes;
	}

	public LabelStyle style() {
		return this.style;
	}

}

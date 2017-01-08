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
		if (style != null) {
			updateClientComponent("addClass", style.toString());
		} else {
			updateClientComponent("removeClass", style.toString());
		}

		this.style = style;

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

package at.spot.jfly.ui;

import at.spot.jfly.AbstractComponent;
import at.spot.jfly.style.ButtonStyle;
import j2html.tags.ContainerTag;

/**
 * Implements a bootstrap button:<br>
 * <br>
 * <button type="button" class="btn btn-default">Default</button>
 */
public class Button extends AbstractComponent {

	protected String caption;
	protected ButtonStyle style = ButtonStyle.Default;

	public Button(final String caption) {
		super("button");
		caption(caption);
		setBaseStyleClass("btn");
	}

	public Button style(final ButtonStyle style) {
		this.style = style;
		controller().invokeComponentManipulation(this, "addClass", style.toString());
		return this;
	}

	public ButtonStyle style() {
		return this.style;
	}

	public Button caption(final String caption) {
		this.caption = caption;
		controller().invokeComponentManipulation(this, "text", caption);
		return this;
	}

	public String caption() {
		return this.caption;
	}

	@Override
	protected String getStyleClasses() {
		final String classes = super.getStyleClasses() + style.toString();

		return classes;
	}

	@Override
	public ContainerTag build() {
		final ContainerTag raw = super.build();
		raw.withType("button");
		raw.withText(caption).withClass(getStyleClasses());

		return raw;
	}

}

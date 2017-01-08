package at.spot.jfly.ui;

import at.spot.jfly.AbstractComponent;
import j2html.tags.ContainerTag;

/**
 * Implements a bootstrap button:<br>
 * <br>
 * <button type="button" class="btn btn-default">Default</button>
 */
public class Badge extends AbstractComponent {

	private String text;

	public Badge(final String text) {
		super("span");
		setBaseStyleClass("badge");
		text(text);
	}

	public Badge text(final String text) {
		this.text = text;

		updateClientComponent("text", text);

		return this;
	}

	public String text() {
		return this.text;
	}

	@Override
	public ContainerTag build() {
		final ContainerTag raw = super.build();
		raw.withText(text).withClass(getStyleClasses());

		return raw;
	}

}

package at.spot.jfly;

import at.spot.jfly.ui.AbstractContainerComponent;
import j2html.TagCreator;
import j2html.tags.ContainerTag;

public class Body extends AbstractContainerComponent {

	public Body() {
		super("body");
	}

	@Override
	protected void buildChildren(final ContainerTag container) {
		final ContainerTag content = TagCreator.div().withId("content");
		container.with(content);

		super.buildChildren(content);
	}
}

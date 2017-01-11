package at.spot.jfly.ui.html;

import at.spot.jfly.ui.base.AbstractContainerComponent;
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

		// container.with(TagCreator.script().withText(GsonUtil.toJson(controller().getRegisteredComponents())));

		super.buildChildren(content);
	}
}

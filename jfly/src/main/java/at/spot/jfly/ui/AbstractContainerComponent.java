package at.spot.jfly.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import at.spot.jfly.Component;
import j2html.tags.ContainerTag;

public class AbstractContainerComponent extends AbstractComponent {
	List<Component> children = new ArrayList<>();

	protected AbstractContainerComponent(final String tagName) {
		super(tagName);
	}

	public List<Component> children() {
		return Collections.unmodifiableList(children);
	}

	public <C extends AbstractContainerComponent> C addChildren(final ContainerTag... tags) {
		final Component[] comps = Arrays.stream(tags).map((t) -> new GenericComponent(t)).collect(Collectors.toList())
				.toArray(new Component[0]);

		addChildren(comps);
		return (C) this;
	}

	public <C extends AbstractContainerComponent> C addChildren(final Component... components) {
		children.addAll(Arrays.asList(components));
		// updateClient("jfly", "addChildComponent", this.uuid(),
		// component.build().render());
		return (C) this;
	}

	public <C extends AbstractContainerComponent> C removeChildren(final Component... components) {
		children.removeAll(Arrays.asList(components));
		// updateClient("jfly", "removeChildComponent", this.uuid(),
		// component.uuid());
		return (C) this;
	}

	@Override
	public ContainerTag build() {
		final ContainerTag raw = super.build();

		buildChildren(raw);

		return raw;
	}

	protected void buildChildren(final ContainerTag container) {
		for (final Component c : children) {
			container.with(c.build());
		}
	}
}

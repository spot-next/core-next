package at.spot.jfly.ui;

import java.util.ArrayList;
import java.util.List;

import at.spot.jfly.AbstractComponent;
import at.spot.jfly.Component;
import j2html.tags.ContainerTag;

public class AbstractContainerComponent extends AbstractComponent {
	List<Component> components = new ArrayList<>();

	protected AbstractContainerComponent(final String tagName) {
		super(tagName);
	}

	public <C extends AbstractContainerComponent> C add(final ContainerTag tag) {
		add(new GenericComponent(tag));
		return (C) this;
	}

	public <C extends AbstractContainerComponent> C add(final Component component) {
		components.add(component);
		controller().invoke(this, "append", component.build().render());
		return (C) this;
	}

	public <C extends AbstractContainerComponent> C remove(final Component component) {
		components.remove(component);

		return (C) this;
	}

	@Override
	public ContainerTag build() {
		final ContainerTag raw = super.build();

		for (final Component c : components) {
			raw.with(c.build());
		}

		return raw;
	}
}

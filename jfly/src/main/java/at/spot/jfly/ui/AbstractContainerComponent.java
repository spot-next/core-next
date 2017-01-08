package at.spot.jfly.ui;

import java.util.ArrayList;
import java.util.List;

import at.spot.jfly.AbstractComponent;
import at.spot.jfly.Component;
import j2html.tags.ContainerTag;

public class AbstractContainerComponent extends AbstractComponent {
	List<AbstractComponent> children = new ArrayList<>();

	protected AbstractContainerComponent(final String tagName) {
		super(tagName);
	}

	public List<AbstractComponent> children() {
		return children;
	}

	public <C extends AbstractComponent> C children(final List<C> children) {
		this.children = (List<AbstractComponent>) children;
		return (C) this;
	}

	public <C extends AbstractContainerComponent> C addChild(final ContainerTag tag) {
		addChild(new GenericComponent(tag));
		return (C) this;
	}

	public <C extends AbstractContainerComponent> C addChild(final AbstractComponent component) {
		children.add(component);
		controller().invokeFunctionCall("jfly", "addChildComponent", this.uuid(), component.build().render());
		return (C) this;
	}

	public <C extends AbstractContainerComponent> C removeChild(final AbstractComponent component) {
		children.remove(component);
		controller().invokeFunctionCall("jfly", "removeChildComponent", this.uuid(), component.uuid());
		return (C) this;
	}

	@Override
	public ContainerTag build() {
		final ContainerTag raw = super.build();

		for (final Component c : children) {
			raw.with(c.build());
		}

		return raw;
	}
}

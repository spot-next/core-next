package at.spot.jfly;

import java.util.ArrayList;
import java.util.List;

import at.spot.jfly.ui.GenericComponent;
import j2html.tags.ContainerTag;

public class Body extends AbstractComponent {

	List<Component> components = new ArrayList<>();

	public Body() {
		super("body");
	}

	public Body add(final ContainerTag tag) {
		add(new GenericComponent(tag));
		return this;
	}

	public Body add(final Component component) {
		components.add(component);
		return this;
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

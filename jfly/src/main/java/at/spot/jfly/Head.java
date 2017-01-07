package at.spot.jfly;

import java.util.ArrayList;
import java.util.List;

import j2html.TagCreator;
import j2html.tags.ContainerTag;

public class Head extends AbstractComponent {

	protected String title;
	protected List<Stylesheet> stylesheets = new ArrayList<>();
	protected List<Script> scripts = new ArrayList<>();

	public Head() {
		super("head");
	}

	public Head title(final String title) {
		this.title = title;
		return this;
	}

	public String title() {
		return title;
	}

	public Head stylesheet(final Stylesheet stylesheet) {
		stylesheets.add(stylesheet);
		return this;
	}

	public List<Stylesheet> stylesheets() {
		return stylesheets;
	}

	public Head script(final Script script) {
		scripts.add(script);
		return this;
	}

	public List<Script> scripts() {
		return scripts;
	}

	@Override
	public ContainerTag build() {
		final ContainerTag raw = super.build();

		raw.with(TagCreator.title(title));

		for (final Stylesheet st : stylesheets) {
			raw.with(st.build());
		}

		for (final Script sc : scripts) {
			raw.with(sc.build());
		}

		return raw;
	}

}

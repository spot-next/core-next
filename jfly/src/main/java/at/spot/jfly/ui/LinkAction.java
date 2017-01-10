package at.spot.jfly.ui;

/**
 * Implements a generic link.
 */
public class LinkAction extends AbstractActionComponent {
	private String location;

	public LinkAction(final String text) {
		super("a", text);
	}

	public LinkAction location(final String location) {
		this.location = location;
		return this;
	}

	public String location() {
		return this.location;
	}
}

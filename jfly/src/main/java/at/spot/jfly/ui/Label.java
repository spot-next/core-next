package at.spot.jfly.ui;

import at.spot.jfly.style.ComponentStyle;

/**
 * Implements a bootstrap label:<br>
 * <br>
 * <button type="button" class="btn btn-default">Default</button>
 */
public class Label extends AbstractTextComponent {
	public Label(final String text) {
		super(text);
		addStyle(ComponentStyle.Label);
	}
}

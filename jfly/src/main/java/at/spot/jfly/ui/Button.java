package at.spot.jfly.ui;

import at.spot.jfly.style.ComponentStyle;

/**
 * Implements a bootstrap button:<br>
 * <br>
 * <button type="button" class="btn btn-default">Default</button>
 */
public class Button extends AbstractActionComponent {
	public Button(final String text) {
		super("button", text);
		addStyle(ComponentStyle.Button);
	}
}

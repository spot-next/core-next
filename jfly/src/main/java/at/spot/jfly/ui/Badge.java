package at.spot.jfly.ui;

import at.spot.jfly.style.ComponentStyle;

/**
 * Implements a bootstrap badge:<br>
 * <br>
 * <button type="button" class="btn btn-default">Default</button>
 */
public class Badge extends AbstractTextComponent {
	public Badge(final String text) {
		super(text);
		addStyle(ComponentStyle.Badge);
	}
}

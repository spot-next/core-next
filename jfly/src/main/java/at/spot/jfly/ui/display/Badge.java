package at.spot.jfly.ui.display;

import at.spot.jfly.style.BadgeStyle;
import at.spot.jfly.ui.base.AbstractTextComponent;

/**
 * Implements a bootstrap badge:<br>
 * <br>
 * <button type="button" class="btn btn-default">Default</button>
 */
public class Badge extends AbstractTextComponent {
	public Badge(final String text) {
		super(text);
		addStyleClasses(BadgeStyle.None);
	}
}

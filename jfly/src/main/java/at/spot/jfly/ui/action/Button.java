package at.spot.jfly.ui.action;

import at.spot.jfly.style.ButtonStyle;
import at.spot.jfly.style.ComponentType;
import at.spot.jfly.ui.base.AbstractActionComponent;

/**
 * Implements a bootstrap button:<br>
 * <br>
 * <button type="button" class="btn btn-default">Default</button>
 */
public class Button extends AbstractActionComponent {
	public Button(final String text) {
		super("button", text);
		componentType(ComponentType.Button);
		addStyleClasses(ButtonStyle.Success.toString());
	}
}

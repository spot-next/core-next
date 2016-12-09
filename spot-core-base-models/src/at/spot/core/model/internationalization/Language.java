package at.spot.core.model.internationalization;

import java.util.Locale;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.model.Item;

@ItemType
public class Language extends Item {

	private static final long serialVersionUID = 1L;

	@Property(unique = true)
	public Locale locale;
}

package at.spot.core.model.internationalization;

import java.util.Locale;

import javax.validation.constraints.NotNull;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.model.Item;
import at.spot.core.model.ItemTypeConstants;

@ItemType(typeCode = ItemTypeConstants.LANGUAGE)
public class Language extends Item {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Property(unique = true)
	public Locale locale;
}

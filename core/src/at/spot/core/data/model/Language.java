package at.spot.core.data.model;

import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.annotation.model.ItemType;

@ItemType
public class Language extends Item {

	private static final long serialVersionUID = 1L;

	@Property(unique = true)
	public String isoCode;
}

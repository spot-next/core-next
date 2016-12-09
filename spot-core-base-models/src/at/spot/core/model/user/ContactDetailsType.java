package at.spot.core.model.user;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.model.Item;

@ItemType
public class ContactDetailsType extends Item {
	@Property(unique = true)
	public String type;
}

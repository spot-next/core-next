package at.spot.core.model.user;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.model.Item;

@ItemType
public class AddressType extends Item {
	@Property(unique = true)
	public String code;
}

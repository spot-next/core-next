package at.spot.core.model.user;

import org.joda.time.DateTime;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.model.ItemTypeConstants;

@ItemType(typeCode = ItemTypeConstants.USER)
public class Customer extends User {
	private static final long serialVersionUID = 1L;

	@Property
	public DateTime birthday;
}

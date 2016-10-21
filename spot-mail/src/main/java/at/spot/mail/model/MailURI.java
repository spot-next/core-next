package at.spot.mail.model;

import at.spot.core.infrastructure.annotation.model.ItemType;
import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.model.Item;
import at.spot.mail.type.UriType;

@ItemType
public abstract class MailURI extends Item {

	private static final long serialVersionUID = 1L;

	@Property
	public String uri;

	@Property
	public UriType type;
}

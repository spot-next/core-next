package at.spot.core.model.media;

import org.springframework.util.MimeType;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.model.Item;
import at.spot.core.model.ItemTypeConstants;

@ItemType(typeCode = ItemTypeConstants.MEDIA)
public class Media extends Item {

	private static final long serialVersionUID = 1L;

	@Property(unique = true)
	public String uid;

	@Property
	public MimeType mimeType;

	@Property
	public String encoding;

	@Property
	public Object content;
}

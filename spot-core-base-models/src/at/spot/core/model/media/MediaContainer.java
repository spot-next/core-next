package at.spot.core.model.media;

import org.springframework.util.MimeType;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.model.Item;
import at.spot.core.model.ItemTypeConstants;

@ItemType(typeCode = ItemTypeConstants.MEDIA_CONTAINER)
public class MediaContainer extends Item {

	private static final long serialVersionUID = 1L;

	@Property
	public MimeType mimeType;

	@Property
	public String encoding;

	@Property
	public Object content;
}

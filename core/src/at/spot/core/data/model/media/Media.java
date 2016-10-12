package at.spot.core.data.model.media;

import org.springframework.util.MimeType;

import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.data.model.Item;
import at.spot.core.infrastructure.annotation.model.ItemType;

@ItemType
public class Media extends Item {

	private static final long serialVersionUID = 1L;

	@Property
	public MimeType mimeType;

	@Property
	public String encoding;

	@Property
	public Object content;
}

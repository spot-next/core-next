package at.spot.core.model.media;

import java.util.List;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.model.ItemTypeConstants;

@ItemType(typeCode = ItemTypeConstants.IMAGE_MEDIA)
public class ImageMedia extends Media {

	private static final long serialVersionUID = 1L;

	@Property
	List<Media> medias;
}

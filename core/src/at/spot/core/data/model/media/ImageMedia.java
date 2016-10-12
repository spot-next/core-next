package at.spot.core.data.model.media;

import java.util.List;

import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.annotation.model.ItemType;

@ItemType
public class ImageMedia extends Media {

	private static final long serialVersionUID = 1L;

	@Property
	List<Media> medias;
}

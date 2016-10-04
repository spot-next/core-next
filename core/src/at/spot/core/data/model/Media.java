package at.spot.core.data.model;

import org.springframework.util.MimeType;

import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.annotation.model.Type;

@Type
public class Media extends Item {

	private static final long serialVersionUID = 1L;

	@Property
	public MimeType mimeType;

	@Property
	public String encoding;

	@Property
	public Object content;
}

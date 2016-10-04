package at.spot.core.data.model;

import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.annotation.model.Type;

@Type
public class Language extends Item {

	private static final long serialVersionUID = 1L;

	@Property(unique = true)
	public String isoCode;
}

package at.spot.core.model;

import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.annotation.model.Type;

@Type
public class ConfigEntry extends Item {

	private static final long serialVersionUID = 1L;

	@Property(unique = true)
	public String key;

	@Property
	public String value;
}

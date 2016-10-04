package at.spot.core.data.model;

import java.util.List;

import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.annotation.model.Type;

@Type
public class Configuration extends Item {

	private static final long serialVersionUID = 1L;

	@Property(unique = true)
	public String uid;

	@Property(isReference = true)
	public List<ConfigEntry> entries;
}

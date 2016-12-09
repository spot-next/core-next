package at.spot.core.model.configuration;

import java.util.List;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.model.Item;

@ItemType
public class Configuration extends Item {

	private static final long serialVersionUID = 1L;

	@Property(unique = true)
	public String uid;

	@Property(isReference = true)
	public List<ConfigEntry> entries;
}

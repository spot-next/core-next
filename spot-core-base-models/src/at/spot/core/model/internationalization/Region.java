package at.spot.core.model.internationalization;

import javax.validation.constraints.NotNull;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;

@ItemType
public class Region extends Country {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Property
	public Country country;
}

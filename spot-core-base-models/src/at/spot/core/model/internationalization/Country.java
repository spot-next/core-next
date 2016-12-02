package at.spot.core.model.internationalization;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import at.spot.core.infrastructure.annotation.model.ItemType;
import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.model.Item;

@ItemType
public class Country extends Item {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Length(min = 2, max = 2)
	@Property(unique = true)
	public String isoCode;

	@Property
	public String name;
}

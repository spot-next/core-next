package at.spot.core.model.internationalization;

import java.util.Locale;

import at.spot.core.infrastructure.annotation.model.ItemType;
import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.model.KeyValuePair;

@ItemType
public class LocalizationKey extends KeyValuePair<String> {

	private static final long serialVersionUID = 1L;

	@Property(unique = true)
	public String key;

	@Property(unique = true)
	public Locale locale;

	@Property
	public String value;
}

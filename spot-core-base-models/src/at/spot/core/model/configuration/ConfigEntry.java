package at.spot.core.model.configuration;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.model.ItemTypeConstants;
import at.spot.core.model.KeyValuePair;

@ItemType(typeCode = ItemTypeConstants.CONFIG_ENTRY)
public class ConfigEntry extends KeyValuePair<String, String> {

	private static final long serialVersionUID = 1L;

}

package at.spot.core.infrastructure.serialization.jackson;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class ItemIgnorePropertiesMixIn {
	@JsonIgnore
	public abstract Map<String, Object> getUniqueProperties();

	@JsonIgnore
	public abstract boolean isPersisted();
}
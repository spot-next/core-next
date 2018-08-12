package io.spotnext.core.infrastructure.serialization.jackson;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// fixes unserializable hibernate proxies
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public abstract class ItemSerializationMixIn {
	@JsonIgnore
	public abstract Map<String, Object> getUniqueProperties();

	@JsonIgnore
	public abstract boolean isPersisted();
}
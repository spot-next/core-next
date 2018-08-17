package io.spotnext.core.infrastructure.serialization.jackson;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
// fixes unserializable hibernate proxies
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public abstract class ItemSerializationMixIn {
	@JsonIgnore
	public abstract Map<String, Object> getUniqueProperties();

	@JsonIgnore
	public abstract boolean isPersisted();
}
package io.spotnext.core.infrastructure.serialization.jackson;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>Abstract ItemSerializationMixIn class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
// fixes unserializable hibernate proxies
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public abstract class ItemSerializationMixIn {
	/**
	 * <p>getUniqueProperties.</p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	@JsonIgnore
	public abstract Map<String, Object> getUniqueProperties();

	/**
	 * <p>isPersisted.</p>
	 *
	 * @return a boolean.
	 */
	@JsonIgnore
	public abstract boolean isPersisted();
}

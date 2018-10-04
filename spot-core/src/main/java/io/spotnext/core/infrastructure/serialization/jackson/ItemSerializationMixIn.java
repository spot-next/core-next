package io.spotnext.core.infrastructure.serialization.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
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
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" ,"uniquenessHash", "deleted", "uniqueProperties", "isPersisted" })
public abstract class ItemSerializationMixIn {
	
}

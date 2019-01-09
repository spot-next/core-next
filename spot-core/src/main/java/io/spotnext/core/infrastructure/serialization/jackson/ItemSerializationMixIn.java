package io.spotnext.core.infrastructure.serialization.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * <p>
 * Abstract ItemSerializationMixIn class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE)
// fixes unserializable hibernate proxies
@JsonPropertyOrder({ "typeCode", "id", "version", "createdAt", "createdBy", "lastModifiedAt", "lastModifiedBy" })
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "uniquenessHash", "deleted", "uniqueProperties", "isPersisted" })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public abstract class ItemSerializationMixIn {

	@JsonSerialize
	public abstract String getTypeCode();

	// render IDs as string in JSON, because otherwise it would cause an overflow, javascript can't interpret it correctly
	// see also ItemProxySerializer and ItemCollectionProxySerializer
	@JsonSerialize(using = ToStringSerializer.class)
	public abstract long getId();

}

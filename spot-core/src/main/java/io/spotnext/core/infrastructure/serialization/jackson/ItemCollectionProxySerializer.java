package io.spotnext.core.infrastructure.serialization.jackson;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import io.spotnext.infrastructure.type.Item;

/**
 * <p>ItemCollectionProxySerializer class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ItemCollectionProxySerializer extends JsonSerializer<Collection<Item>> {

	/** {@inheritDoc} */
	@Override
	public void serialize(final Collection<Item> source, final JsonGenerator gen, final SerializerProvider serializers)
			throws IOException {

		gen.writeStartArray();

		for (final Item item : source) {
			if (item != null) {
				gen.writeStartObject();
				
				String typeCode = item.getTypeCode();
				
				if (gen instanceof ToXmlGenerator) {
					((ToXmlGenerator) gen).setNextIsAttribute(true);
					gen.writeFieldName("typeCode");
					gen.writeString(typeCode);
					((ToXmlGenerator) gen).setNextIsAttribute(false);
				} else {
					gen.writeObjectField("typeCode", typeCode);
				}
				
				// see ItemSerializationMixIn
				gen.writeObjectField("id", item.getId() + "");
				
				gen.writeEndObject();
			}
		}

		gen.writeEndArray();
	}
}

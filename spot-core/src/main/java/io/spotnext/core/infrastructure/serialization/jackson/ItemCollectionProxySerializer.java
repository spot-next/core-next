package io.spotnext.core.infrastructure.serialization.jackson;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.infrastructure.type.Item;

/**
 * <p>ItemCollectionProxySerializer class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ItemCollectionProxySerializer extends JsonSerializer<Collection<Item>> {

	private TypeService typeService;
	private ModelService modelService;

	/** {@inheritDoc} */
	@Override
	public void serialize(final Collection<Item> source, final JsonGenerator gen, final SerializerProvider serializers)
			throws IOException {

		gen.writeStartArray();

		for (final Item item : source) {
			if (item != null) {
				gen.writeStartObject();
				
				String typeCode = getTypeService().getTypeCodeForClass(item.getClass());
				
				if (gen instanceof ToXmlGenerator) {
					((ToXmlGenerator) gen).setNextIsAttribute(true);
					gen.writeFieldName("typeCode");
					gen.writeString(typeCode);
					((ToXmlGenerator) gen).setNextIsAttribute(false);
				} else {
					gen.writeObjectField("typeCode", typeCode);
				}
				
				gen.writeObjectField("pk", item.getPk());
				
				gen.writeEndObject();
			}
		}

		gen.writeEndArray();
	}

	/**
	 * <p>Getter for the field <code>typeService</code>.</p>
	 *
	 * @return a {@link io.spotnext.infrastructure.service.TypeService} object.
	 */
	public TypeService getTypeService() {
		if (typeService == null) {
			typeService = (TypeService) Registry.getApplicationContext().getBean("typeService");
		}

		return typeService;
	}

	/**
	 * <p>Getter for the field <code>modelService</code>.</p>
	 *
	 * @return a {@link io.spotnext.infrastructure.service.ModelService} object.
	 */
	public ModelService getModelService() {
		if (modelService == null) {
			modelService = (ModelService) Registry.getApplicationContext().getBean("modelService");
		}

		return modelService;
	}

}

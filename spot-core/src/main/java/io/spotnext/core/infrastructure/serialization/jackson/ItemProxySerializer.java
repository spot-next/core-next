package io.spotnext.core.infrastructure.serialization.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.infrastructure.type.Item;

/**
 * <p>ItemProxySerializer class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ItemProxySerializer extends JsonSerializer<Item> {

	private TypeService typeService;
	private ModelService modelService;

	/** {@inheritDoc} */
	@Override
	public void serializeWithType(Item value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer)
			throws IOException {

		gen.writeStartObject();
		serialize(value, gen, serializers);
		gen.writeEndObject();
	}

	/** {@inheritDoc} */
	@Override
	public void serialize(final Item source, final JsonGenerator gen, final SerializerProvider serializers)
			throws IOException {

		// see ItemSerializationMixIn
		gen.writeObjectField("pk", "a");
		gen.writeObjectField("typeCode", getTypeService().getTypeCodeForClass(source.getClass()));
	}

	/** {@inheritDoc} */
	@Override
	public Class<Item> handledType() {
		return Item.class;
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

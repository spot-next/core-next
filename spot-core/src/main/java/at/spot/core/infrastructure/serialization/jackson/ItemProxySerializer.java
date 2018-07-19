package at.spot.core.infrastructure.serialization.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.core.types.Item;

public class ItemProxySerializer extends JsonSerializer<Item> {

	private TypeService typeService;
	private ModelService modelService;

	@Override
	public void serializeWithType(Item value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer)
			throws IOException {

		gen.writeStartObject();
		serialize(value, gen, serializers);
		gen.writeEndObject();
	}

	@Override
	public void serialize(final Item source, final JsonGenerator gen, final SerializerProvider serializers)
			throws IOException {

		gen.writeObjectField("pk", source.getPk());
		gen.writeObjectField("typeCode", getTypeService().getTypeCodeForClass(source.getClass()));
	}

	@Override
	public Class<Item> handledType() {
		return Item.class;
	}

	public TypeService getTypeService() {
		if (typeService == null) {
			typeService = (TypeService) Registry.getApplicationContext().getBean("typeService");
		}

		return typeService;
	}

	public ModelService getModelService() {
		if (modelService == null) {
			modelService = (ModelService) Registry.getApplicationContext().getBean("modelService");
		}

		return modelService;
	}

}

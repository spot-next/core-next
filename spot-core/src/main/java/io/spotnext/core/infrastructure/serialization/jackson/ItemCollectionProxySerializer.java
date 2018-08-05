package io.spotnext.core.infrastructure.serialization.jackson;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.core.types.Item;

public class ItemCollectionProxySerializer extends JsonSerializer<Collection<Item>> {

	private TypeService typeService;
	private ModelService modelService;

	@Override
	public void serialize(final Collection<Item> source, final JsonGenerator gen, final SerializerProvider serializers)
			throws IOException {

		gen.writeStartArray();

		for (final Item item : source) {
			if (item != null) {
				gen.writeStartObject();
				gen.writeObjectField("pk", item.getPk());
				gen.writeObjectField("typeCode", getTypeService().getTypeCodeForClass(item.getClass()));
				gen.writeEndObject();
			}
		}

		gen.writeEndArray();
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

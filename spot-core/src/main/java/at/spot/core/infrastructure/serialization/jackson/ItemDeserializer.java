package at.spot.core.infrastructure.serialization.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.core.model.Item;

public class ItemDeserializer<I extends Item> extends JsonDeserializer<I> {

	private TypeService typeService;
	private ModelService modelService;

	@Override
	public I deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		// Parse "object" node into Jackson's tree model
		JsonNode node = p.getCodec().readTree(p);

		JsonNode typeCodeNode = node.get("typeCode");

		if (typeCodeNode != null && !typeCodeNode.isNull()) {
			try {
				Class<I> itemType = (Class<I>) getTypeService().getClassForTypeCode(typeCodeNode.asText());
			} catch (UnknownTypeException e) {
				throw new IOException("Could not deserialize object", e);
			}
		}

		return null;
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

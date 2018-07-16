package at.spot.core.infrastructure.serialization.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.impl.TypeDeserializerBase;

import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.core.model.Item;

public class ItemDeserializer<I extends Item> extends JsonDeserializer<I> {

	private TypeService typeService;
	private ModelService modelService;

	private final Class<I> itemType;

	public ItemDeserializer(final Class<I> itemType) {
		this.itemType = itemType;
	}

	@Override
	public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt,
			final TypeDeserializer typeDeserializer) throws IOException {

		return loadItem(p, ctxt, (TypeDeserializerBase) typeDeserializer);
	}

	@Override
	public I deserialize(final JsonParser p, final DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		return loadItem(p, ctxt, null);
	}

	private I loadItem(final JsonParser parser, final DeserializationContext context,
			final TypeDeserializerBase typeDeserializer) throws IOException {

		final ObjectCodec oc = parser.getCodec();
		final JsonNode node = oc.readTree(parser);
		I deserializedUser = null;

		final JavaType type = typeDeserializer != null ? typeDeserializer.baseType()
				: context.getTypeFactory().constructSimpleType(itemType, null);

		final DeserializationConfig config = context.getConfig();
		final JsonDeserializer<Object> defaultDeserializer = BeanDeserializerFactory.instance
				.buildBeanDeserializer(context, type, config.introspect(type));

		if (defaultDeserializer instanceof ResolvableDeserializer) {
			((ResolvableDeserializer) defaultDeserializer).resolve(context);
		}

		final JsonParser treeParser = oc.treeAsTokens(node);
		config.initialize(treeParser);

		if (treeParser.getCurrentToken() == null) {
			treeParser.nextToken();
		}

		deserializedUser = (I) defaultDeserializer.deserialize(treeParser, context);

		try {
			getModelService().refresh(deserializedUser);
		} catch (final ModelNotFoundException e) {
			// ignore exception, as this just means that the item is most likely new
		}

		return deserializedUser;
	}

	@Override
	public Class<?> handledType() {
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

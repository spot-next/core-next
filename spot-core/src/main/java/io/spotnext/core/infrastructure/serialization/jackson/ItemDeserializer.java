package io.spotnext.core.infrastructure.serialization.jackson;

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

import io.spotnext.core.infrastructure.exception.ModelNotFoundException;
import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.infrastructure.type.Item;

/**
 * <p>ItemDeserializer class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ItemDeserializer<I extends Item> extends JsonDeserializer<I> {

	private TypeService typeService;
	private ModelService modelService;

	private final Class<I> itemType;

	/**
	 * <p>Constructor for ItemDeserializer.</p>
	 *
	 * @param itemType a {@link java.lang.Class} object.
	 */
	public ItemDeserializer(final Class<I> itemType) {
		this.itemType = itemType;
	}

	/** {@inheritDoc} */
	@Override
	public I deserialize(final JsonParser p, final DeserializationContext ctxt, final I intoValue) throws IOException {
		return loadItem(p, ctxt, null, intoValue);
	}

	/** {@inheritDoc} */
	@Override
	public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt,
			final TypeDeserializer typeDeserializer) throws IOException {

		return loadItem(p, ctxt, (TypeDeserializerBase) typeDeserializer, null);
	}

	/** {@inheritDoc} */
	@Override
	public I deserialize(final JsonParser p, final DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		return loadItem(p, ctxt, null, null);
	}

	private I loadItem(final JsonParser parser, final DeserializationContext context,
			final TypeDeserializerBase typeDeserializer, final I intoValue) throws IOException {

		final ObjectCodec oc = parser.getCodec();
		final JsonNode node = oc.readTree(parser);
		I deserializedItem = null;

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

		if (intoValue != null) {
			deserializedItem = (I) defaultDeserializer.deserialize(treeParser, context, intoValue);
		} else {
			deserializedItem = (I) defaultDeserializer.deserialize(treeParser, context);
		}

		try {
			if (!getModelService().isAttached(deserializedItem)) {
//				getModelService().attach(deserializedItem);
				getModelService().refresh(deserializedItem);
			}
		} catch (final ModelNotFoundException e) {
			// ignore exception, as this just means that the item is most likely
			// new
		}

		return deserializedItem;
	}

	/** {@inheritDoc} */
	@Override
	public Class<?> handledType() {
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

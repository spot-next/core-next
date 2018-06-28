package at.spot.core.infrastructure.strategy.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.SerializationException;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import at.spot.core.infrastructure.serialization.ClassSerializer;
import at.spot.core.infrastructure.serialization.GsonExclusionStrategy;
import at.spot.core.infrastructure.serialization.gson.ItemTypeSerializer;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.strategy.SerializationStrategy;
import at.spot.core.infrastructure.support.ItemTypeDefinition;
import io.gsonfire.DateSerializationPolicy;
import io.gsonfire.GsonFireBuilder;

/**
 * Implements a serialization strategy from and to json format using Gson.
 */
@Service
public class DefaultJsonSerializationStrategy implements SerializationStrategy {

	@Resource
	protected TypeService typeService;

	protected Gson gson;
	protected boolean serializeNulls = false;
	protected boolean excludeFieldsWithoutExposeAnnotation = true;

	@PostConstruct
	public void init() throws ClassNotFoundException {
		final GsonFireBuilder builder = new GsonFireBuilder();
		builder.dateSerializationPolicy(DateSerializationPolicy.rfc3339);

		// builder.registerPostProcessor(Object.class, new PostProcessor<Object>() {
		//
		// @Override
		// public void postDeserialize(Object result, JsonElement source, Gson context)
		// {
		// //
		// }
		//
		// @Override
		// public void postSerialize(JsonElement result, Object source, Gson context) {
		// if (result.isJsonObject()) {
		// JsonObject obj = result.getAsJsonObject();
		// // obj.add("type", new JsonPrimitive(source.getClass().getName()));
		//
		// if (source instanceof Item) {
		// obj.add("typeCode",
		// new JsonPrimitive(typeService.getTypeCodeForClass((Class<Item>)
		// source.getClass())));
		// }
		// }
		// }
		// });
		//
		// builder.registerTypeSelector(Object.class, new TypeSelector<Object>() {
		// @Override
		// public Class<? extends Object> getClassForElement(JsonElement readElement) {
		// if (readElement.isJsonArray()) {
		// return Collection.class;
		// // JsonArray array = readElement.getAsJsonArray();
		// //
		// // if (array.size() > 0 && array.get(0).isJsonObject()) {
		// // Class<? extends Object> arrayValueType =
		// // determineType(array.get(0).getAsJsonObject());
		// // } else {
		// // return null;
		// // }
		// }
		//
		// if (readElement.isJsonObject()) {
		// return determineType(readElement.getAsJsonObject());
		// }
		//
		// return null;
		// }
		//
		// private Class<? extends Object> determineType(JsonObject jsonObject) {
		// JsonElement typeCodeElement = jsonObject.get("typeCode");
		//
		// if (typeCodeElement != null) {
		// String typeCode = typeCodeElement.getAsString();
		// try {
		// if (StringUtils.isNotBlank(typeCode)) {
		// return typeService.getClassForTypeCode(typeCode);
		// }
		//
		// String javaType = jsonObject.get("type").getAsString();
		//
		// if (javaType != null) {
		// return Class.forName(javaType);
		// }
		// } catch (Exception e) {
		// throw new SerializationException("Cannot determine java type for json.");
		// }
		// }
		//
		// return null;
		// }
		// });

		// GsonBuilder gsonBuilder = new GsonBuilder();
		GsonBuilder gsonBuilder = builder.createGsonBuilder();

		if (serializeNulls) {
			gsonBuilder.serializeNulls();
		}

		for (ItemTypeDefinition itemTypeDef : typeService.getItemTypeDefinitions().values()) {
			gsonBuilder.registerTypeAdapter(Class.forName(itemTypeDef.getTypeClass()), new ItemTypeSerializer());
		}

		// for handling hibernate entities
		// gsonBuilder.registerTypeAdapterFactory(ItemTypeAdapter.FACTORY);
		gsonBuilder.setExclusionStrategies(new GsonExclusionStrategy(excludeFieldsWithoutExposeAnnotation));
		gsonBuilder.registerTypeAdapter(Class.class, new ClassSerializer());

		// register helper builders for datetimes etc.
		gson = gsonBuilder.create();
	}

	@Override
	public <T> String serialize(final T object) throws SerializationException {
		if (object == null) {
			return null;
		}

		return gson.toJson(object);
	}

	@Override
	public <T> T deserialize(final String serializedObject, final Class<T> type) throws SerializationException {
		try {
			// JsonObject jsonObj = gson.fromJson(serializedObject, JsonObject.class);

			return gson.fromJson(serializedObject, type);
		} catch (final Exception e) {
			throw new SerializationException("Cannot deserialize object", e);
		}
	}

	public void setSerializeNulls(final boolean serializeNulls) {
		this.serializeNulls = serializeNulls;
	}
}

package at.spot.core.infrastructure.serialization.gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.SerializationException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.support.ItemTypePropertyDefinition;
import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.core.model.Item;
import at.spot.core.persistence.service.PersistenceService;

/**
 * This TypeAdapter serializes item type objects. Any * .
 */
public class ItemTypeAdapter<T extends Item> extends TypeAdapter<T> implements JsonSerializer<T> {

	private TypeService typeService;
	private ModelService modelService;

	public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
		@Override
		@SuppressWarnings("unchecked")
		public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
			return (Item.class.isAssignableFrom(type.getRawType()) ? (TypeAdapter<T>) new ItemTypeAdapter(gson, type)
					: null);
		}
	};
	private final Gson context;
	private TypeToken<T> type;

	private ItemTypeAdapter(final Gson context, final TypeToken<T> type) {
		this.context = context;
		this.type = type;
	}

	@Override
	public T read(final JsonReader in) throws IOException {
		// in.beginObject();
		// String typeCode = null;
		// Long pk = null;
		//
		// while (in.hasNext()) {
		// if (in.nextName().equals("type")) {
		// typeCode = in.nextString();
		// break;
		// }
		//
		// if (in.nextName().equals("pk")) {
		// pk = in.nextLong();
		// break;
		// }
		// }
		// in.endObject();

		// if (StringUtils.isNotBlank(typeCode)) {
		// Class<? extends Item> itemType;
		// try {
		// itemType = getTypeService().getClassForTypeCode(typeCode);
		// } catch (UnknownTypeException e) {
		// throw new IOException(e);
		// }
		//
		// } else {
		// throw new IOException("Could not deserialize item of type " + typeCode);
		// }

		return context.fromJson(in, type.getRawType());
	}

	@Override
	public JsonElement serialize(T value, Type typeOfSrc, JsonSerializationContext context) {
		if (value == null) {
			return null;
		}

		final String typeCode = getTypeService().getTypeCodeForClass(value.getClass());
		final JsonObject jsonObj = new JsonObject();

		try {
			final Map<String, ItemTypePropertyDefinition> props = getTypeService().getItemTypeProperties(typeCode);
			for (final ItemTypePropertyDefinition p : props.values()) {
				final Object propValue = getModelService().getPropertyValue(value, p.getName());

				if (propValue != null) {
					if (propValue.getClass().isArray() || Collection.class.isAssignableFrom(propValue.getClass())) {

						final JsonArray array = new JsonArray();

						final Collection<Object> propValueList = propValue.getClass().isArray()
								? Arrays.asList(propValue)
								: (Collection<Object>) propValue;

						for (Object o : propValueList) {
							if (o instanceof Item) {
								array.add(serializeSubItem((Item) o));
							} else {
								array.add(context.serialize(o));
							}
						}
					} else if (Map.class.isAssignableFrom(propValue.getClass())) {
						Map<Object, Object> mapValue = (Map) propValue;
						JsonObject map = new JsonObject();

						for (Map.Entry e : mapValue.entrySet()) {
							Object k = e.getKey();
							Object v = e.getValue();

							String key = null;

							if (PersistenceService.NATIVE_DATATYPES.contains(k.getClass())) {
								key = k.toString();
							} else {
								throw new IOException(
										"Complex objects are not supported as property key: " + k.getClass().getName());
							}

							if (v instanceof Item) {
								writeItem(key, (Item) v, map);
							} else {
								writeObject(key, v, map, context);
							}
						}

						jsonObj.add(p.getName(), map);
					} else {
						if (propValue instanceof Item) {
							writeItem(p.getName(), (Item) propValue, jsonObj);
						} else {
							writeObject(p.getName(), propValue, jsonObj, context);
						}
					}
				}
			}
		} catch (final Exception e) {
			throw new SerializationException("Could not get property definitions of type " + typeCode, e);
		}

		return jsonObj;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void write(final JsonWriter out, final T value) throws IOException {
		if (value == null) {
			out.nullValue();
			return;
		}

		final String typeCode = getTypeService().getTypeCodeForClass(value.getClass());

		out.beginObject();

		try {
			final Map<String, ItemTypePropertyDefinition> props = getTypeService().getItemTypeProperties(typeCode);
			for (final ItemTypePropertyDefinition p : props.values()) {
				out.name(p.getName());

				final Object propValue = getModelService().getPropertyValue(value, p.getName());

				if (propValue != null) {
					if (propValue.getClass().isArray() || Collection.class.isAssignableFrom(propValue.getClass())) {

						out.beginArray();

						final Collection<Object> propValueList = propValue.getClass().isArray()
								? Arrays.asList(propValue)
								: (Collection<Object>) propValue;

						for (Object o : propValueList) {
							if (o instanceof Item) {
								writeItem((Item) o, out);
							} else {
								writeObject(propValue, out);
							}
						}

						out.endArray();
					} else if (Map.class.isAssignableFrom(propValue.getClass())) {
						Map<Object, Object> mapValue = (Map) propValue;

						out.beginObject();

						for (Map.Entry e : mapValue.entrySet()) {
							Object k = e.getKey();
							Object v = e.getValue();

							if (PersistenceService.NATIVE_DATATYPES.contains(k.getClass())) {
								out.name(k.toString());
							} else {
								throw new IOException(
										"Complex objects are not supported as property key: " + k.getClass().getName());
							}

							if (v instanceof Item) {
								writeItem((Item) v, out);
							} else {
								writeObject(v, out);
							}
						}

						out.endObject();
					} else {

						if (propValue instanceof Item) {
							writeItem((Item) propValue, out);
						} else {
							writeObject(propValue, out);
						}
					}
				} else {
					out.nullValue();
				}
			}
		} catch (final UnknownTypeException e) {
			throw new IOException("Could not get property definitions of type " + typeCode, e);
		} catch (final Exception e) {
			throw new IOException(e);
		}

		out.endObject();
	}

	private void writeObject(Object object, JsonWriter out) throws IOException {
		final TypeAdapter delegate = context.getAdapter(TypeToken.get(object.getClass()));
		delegate.write(out, object);
	}

	private void writeItem(Item item, JsonWriter out) throws IOException {
		out.beginObject();
		out.name("pk");
		out.value(item.getPk());
		out.name("type");
		out.value(getTypeService().getTypeCodeForClass(item.getClass()));
		out.endObject();
	}

	protected JsonObject serializeSubItem(Item item) {
		JsonObject itemJsonObj = new JsonObject();

		itemJsonObj.add("pk", new JsonPrimitive(item.getPk()));
		itemJsonObj.add("type", new JsonPrimitive(getTypeService().getTypeCodeForClass(item.getClass())));

		return itemJsonObj;
	}

	private void writeObject(String propertyName, Object object, JsonObject json, JsonSerializationContext context)
			throws IOException {

		json.add(propertyName, context.serialize(object));
	}

	private void writeItem(String propertyName, Item item, JsonObject json) throws IOException {
		json.add(propertyName, serializeSubItem(item));
	}

	public TypeService getTypeService() {
		if (typeService == null) {
			typeService = Registry.getApplicationContext().getBean(TypeService.class);
		}

		return typeService;
	}

	public ModelService getModelService() {
		if (modelService == null) {
			modelService = Registry.getApplicationContext().getBean(ModelService.class);
		}

		return modelService;
	}

}
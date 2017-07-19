package at.spot.core.infrastructure.serialization;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * De-/Serializes java classes.
 */
public class ClassSerializer implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

	@Override
	public JsonElement serialize(final Class<?> javaClass, final Type typeOfSrc,
			final JsonSerializationContext context) {
		final JsonObject root = new JsonObject();

		root.addProperty("name", javaClass.getName());
		root.addProperty("simpleName", javaClass.getSimpleName());
		root.addProperty("package", javaClass.getPackage().getName());

		return root;
	}

	@Override
	public Class<?> deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
			throws JsonParseException {

		final String fullName = json.getAsJsonObject().get("name").getAsString();

		try {
			return Class.forName(fullName);
		} catch (final ClassNotFoundException e) {
			throw new JsonParseException("Could not deserialize class", e);
		}
	}

}
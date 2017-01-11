package at.spot.jfly.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import io.gsonfire.GsonFireBuilder;

public class GsonUtil {
	private static final Gson gson = new GsonFireBuilder().enableExposeMethodResult().createGsonBuilder().create();
	// .registerTypeAdapterFactory(new StyleEnumAdapterFactory()).create();

	public static String toJson(final Object object) {
		return gson.toJson(object);
	}

	public static <T> T fromJson(final JsonElement json, final Class<T> classOfT) throws JsonSyntaxException {
		return gson.fromJson(json, classOfT);
	}

	public static <T> T fromJson(final String json, final Class<T> classOfT) throws JsonSyntaxException {
		return gson.fromJson(json, classOfT);
	}
}

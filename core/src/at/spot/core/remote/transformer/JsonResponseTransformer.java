package at.spot.core.remote.transformer;

import com.google.gson.Gson;

import spark.ResponseTransformer;

/**
 * Converts the given object to json.
 */
public class JsonResponseTransformer implements ResponseTransformer {

	@Override
	public String render(Object object) throws Exception {
		Gson gson = new Gson();

		return gson.toJson(object);
	}
}

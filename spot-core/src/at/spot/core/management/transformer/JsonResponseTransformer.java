package at.spot.core.management.transformer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.google.gson.Gson;

import spark.ResponseTransformer;

/**
 * Converts the given object to json.
 */
public class JsonResponseTransformer implements ResponseTransformer {

	@Override
	public String render(Object object) throws Exception {
		Gson gson = new Gson();

		// Make Serial
		Writer writer = new OutputStreamWriter(new ByteArrayOutputStream());
		gson.toJson(object, writer);
		writer.close();

		return writer.toString();
	}
}

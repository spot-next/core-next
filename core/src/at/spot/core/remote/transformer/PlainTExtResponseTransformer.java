package at.spot.core.remote.transformer;

import spark.ResponseTransformer;

public class PlainTExtResponseTransformer implements ResponseTransformer {

	@Override
	public String render(Object arg) throws Exception {
		return arg.toString();
	}
}

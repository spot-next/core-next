package at.spot.core.management.transformer;

import spark.ResponseTransformer;

public class PlainTExtResponseTransformer implements ResponseTransformer {

	@Override
	public String render(Object arg) throws Exception {
		return arg.toString();
	}
}

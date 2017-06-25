package at.spot.core.management.transformer;

import org.springframework.stereotype.Service;

import spark.ResponseTransformer;

@Service
public class PlainTextResponseTransformer implements ResponseTransformer {

	@Override
	public String render(final Object arg) throws Exception {
		return arg.toString();
	}
}

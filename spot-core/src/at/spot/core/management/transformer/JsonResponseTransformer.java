package at.spot.core.management.transformer;

import at.spot.core.infrastructure.service.SerializationService;
import at.spot.core.infrastructure.spring.support.Registry;
import spark.ResponseTransformer;

/**
 * Converts the given object to json.
 */
public class JsonResponseTransformer implements ResponseTransformer {

	@Override
	public String render(final Object object) throws Exception {
		return getSeriaizationService().toJson(object);
	}

	protected SerializationService getSeriaizationService() {
		return Registry.getApplicationContext().getBean("serializationService", SerializationService.class);
	}
}

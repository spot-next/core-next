package at.spot.core.management.transformer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.service.SerializationService;
import at.spot.core.infrastructure.support.spring.Registry;
import spark.ResponseTransformer;

/**
 * Converts the given object to json.
 */
@Service
public class JsonResponseTransformer implements ResponseTransformer {

	@Autowired
	protected SerializationService serializationService;

	@Override
	public String render(final Object object) throws Exception {
		return getSeriaizationService().toJson(object);
	}

	protected SerializationService getSeriaizationService() {
		if (serializationService == null)
			serializationService = Registry.getApplicationContext().getBean("serializationService",
					SerializationService.class);

		return serializationService;
	}
}

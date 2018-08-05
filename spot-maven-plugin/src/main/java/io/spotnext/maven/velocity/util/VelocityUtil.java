package io.spotnext.maven.velocity.util;

import java.util.Map;

import org.apache.velocity.context.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

public class VelocityUtil {

	public static Context createSingletonObjectContext(Object dataObject) {
		final ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String, Object> map = mapper.convertValue(dataObject, Map.class);

		return new EnhancedVelocityContext(map);
	}
}

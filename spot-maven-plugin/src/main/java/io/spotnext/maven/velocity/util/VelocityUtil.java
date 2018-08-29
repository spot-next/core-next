package io.spotnext.maven.velocity.util;

import java.util.Map;

import org.apache.velocity.context.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>VelocityUtil class.</p>
 *
 * @since 1.0
 */
public class VelocityUtil {

	/**
	 * <p>createSingletonObjectContext.</p>
	 *
	 * @param dataObject a {@link java.lang.Object} object.
	 * @return a {@link org.apache.velocity.context.Context} object.
	 */
	public static Context createSingletonObjectContext(Object dataObject) {
		final ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String, Object> map = mapper.convertValue(dataObject, Map.class);

		return new EnhancedVelocityContext(map);
	}
}

package at.spot.maven.velocity.util;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;

public class EnhancedVelocityContext extends VelocityContext {

	public EnhancedVelocityContext(Map<String, Object> map) {
		super(map);
	}

	public String join(Iterable<?> strings, String delimiter) {
		return StringUtils.join(strings, delimiter);
	}
}

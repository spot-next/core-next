package io.spotnext.maven.velocity.util;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;

/**
 * <p>EnhancedVelocityContext class.</p>
 *
 * @since 1.0
 */
public class EnhancedVelocityContext extends VelocityContext {

	/**
	 * <p>Constructor for EnhancedVelocityContext.</p>
	 *
	 * @param map a {@link java.util.Map} object.
	 */
	public EnhancedVelocityContext(Map<String, Object> map) {
		super(map);
	}

	/**
	 * <p>join.</p>
	 *
	 * @param strings a {@link java.lang.Iterable} object.
	 * @param delimiter a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public String join(Iterable<?> strings, String delimiter) {
		return StringUtils.join(strings, delimiter);
	}
}

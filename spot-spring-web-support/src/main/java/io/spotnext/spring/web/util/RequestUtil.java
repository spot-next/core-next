package io.spotnext.spring.web.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * <p>RequestUtil class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class RequestUtil {
	/**
	 * Returns the current spring {@link javax.servlet.http.HttpServletRequest} or null.
	 *
	 * @return a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static HttpServletRequest getCurrentHttpRequest() {
		final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

		if (requestAttributes instanceof ServletRequestAttributes) {
			final HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			return request;
		}

		return null;
	}
}

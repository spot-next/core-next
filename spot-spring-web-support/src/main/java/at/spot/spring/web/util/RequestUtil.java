package at.spot.spring.web.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestUtil {
	/**
	 * Returns the current spring {@link HttpServletRequest} or null.
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

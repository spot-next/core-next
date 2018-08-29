package io.spotnext.core.infrastructure.http;

import spark.ResponseTransformer;

/**
 * <p>HttpResponse interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface HttpResponse {
	/**
	 * <p>getPayload.</p>
	 *
	 * @return the payload. This can be of any type - conversion will be handled by
	 *         the {@link spark.ResponseTransformer}.
	 */
	Object getPayload();

	/**
	 * Returns the HTTP status code.
	 *
	 * @return the HTTP status code
	 */
	HttpStatus getHttpStatus();

	/**
	 * Sets the payload
	 *
	 * @param payload is the payload data to be returned
	 * @return the current instance
	 * @param <R> a R object.
	 */
	<R extends HttpResponse> R withPayload(Object payload);
}

package io.spotnext.core.infrastructure.http;

import spark.ResponseTransformer;

public interface HttpResponse {
	/**
	 * @return the payload. This can be of any type - conversion will be handled by
	 *         the {@link ResponseTransformer}.
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
	 */
	<R extends HttpResponse> R withPayload(Object payload);
}

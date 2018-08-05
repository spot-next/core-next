package io.spotnext.core.infrastructure.http;

import java.util.Objects;

/**
 * This entity extends the spring ResponseEntity with the ability to set the
 * body and status after object creation.
 */
public class HttpResponse<T> {
	protected Payload<T> body;
	protected HttpStatus statusCode;

	public HttpResponse() {
		this(HttpStatus.OK);
	}

	public HttpResponse(final HttpStatus status) {
		this(Payload.empty(), status);
	}

	public HttpResponse(final Payload<T> body, final HttpStatus status) {
		this.body = body;
		this.statusCode = status;
	}

	public static HttpResponse<?> internalError() {
		return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Sets the HTTP status code of the response.
	 */
	public void setStatusCode(final HttpStatus status) {
		this.statusCode = status;
	}

	/**
	 * Return the HTTP status code of the response.
	 * 
	 * @return the HTTP status as an HttpStatus enum entry
	 */
	public HttpStatus getStatusCode() {
		return this.statusCode;
	}

	/**
	 * Return the HTTP status code of the response.
	 * 
	 * @return the HTTP status as an int value
	 * @since 4.3
	 */
	public int getStatusCodeValue() {
		return this.statusCode.value();
	}

	public Payload<T> getBody() {
		return this.body;
	}

	public void setBody(final Payload<T> body) {
		this.body = body;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return Objects.equals(this, obj);
	}
}

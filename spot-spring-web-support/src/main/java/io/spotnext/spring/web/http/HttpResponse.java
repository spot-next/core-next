package io.spotnext.spring.web.http;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * This entity extends the spring {@link org.springframework.http.ResponseEntity} with the ability to set
 * the body and status after object creation.
 *
 * @param <T> the type of the payload
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class HttpResponse<T> extends ResponseEntity<Payload<T>> {
	protected Payload<T> body;
	protected HttpStatus statusCode;

	/**
	 * <p>Constructor for HttpResponse.</p>
	 */
	public HttpResponse() {
		this(HttpStatus.OK);
	}

	/**
	 * <p>Constructor for HttpResponse.</p>
	 *
	 * @param status a {@link org.springframework.http.HttpStatus} object.
	 */
	public HttpResponse(final HttpStatus status) {
		this(Payload.empty(), status);
	}

	/**
	 * <p>Constructor for HttpResponse.</p>
	 *
	 * @param body a {@link io.spotnext.spring.web.http.Payload} object.
	 * @param status a {@link org.springframework.http.HttpStatus} object.
	 */
	public HttpResponse(final Payload<T> body, final HttpStatus status) {
		super(status);
		this.body = body;
		this.statusCode = status;
	}

	/**
	 * <p>internalError.</p>
	 *
	 * @return a {@link io.spotnext.spring.web.http.HttpResponse} object.
	 */
	public static HttpResponse<?> internalError() {
		return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Sets the HTTP status code of the response.
	 *
	 * @param status the HTTP status enum
	 */
	public void setStatusCode(final HttpStatus status) {
		this.statusCode = status;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return the HTTP status code of the response.
	 */
	@Override
	public HttpStatus getStatusCode() {
		return this.statusCode;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return the HTTP status code of the response.
	 * @since 4.3
	 */
	@Override
	public int getStatusCodeValue() {
		return this.statusCode.value();
	}

	/** {@inheritDoc} */
	@Override
	public Payload<T> getBody() {
		return this.body;
	}

	/**
	 * <p>Setter for the field <code>body</code>.</p>
	 *
	 * @param body a {@link io.spotnext.spring.web.http.Payload} object.
	 */
	public void setBody(final Payload<T> body) {
		this.body = body;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return Objects.hashCode(this);
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object obj) {
		return Objects.equals(this, obj);
	}
}

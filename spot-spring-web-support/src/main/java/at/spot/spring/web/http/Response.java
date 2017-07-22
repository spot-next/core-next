package at.spot.spring.web.http;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import at.spot.spring.web.dto.Payload;

/**
 * This entity extends the spring {@link ResponseEntity} with the ability to set
 * the body and status after object creation.
 * 
 * @param <T>
 */
public class Response<T> extends ResponseEntity<Payload<T>> {
	protected Payload<T> body;
	protected HttpStatus statusCode;

	public Response() {
		this(HttpStatus.OK);
	}

	public Response(final HttpStatus status) {
		super(status);
		this.statusCode = super.getStatusCode();
		this.body = Payload.empty();
	}

	public Response(final Payload<T> body, final HttpStatus status) {
		super(status);
		this.body = body;
		this.statusCode = status;
	}

	/**
	 * Sets the HTTP status code of the response.
	 * 
	 * @return the HTTP status as an HttpStatus enum entry
	 */
	public void setStatusCode(final HttpStatus status) {
		this.statusCode = status;
	}

	/**
	 * Return the HTTP status code of the response.
	 * 
	 * @return the HTTP status as an HttpStatus enum entry
	 */
	@Override
	public HttpStatus getStatusCode() {
		return this.statusCode;
	}

	/**
	 * Return the HTTP status code of the response.
	 * 
	 * @return the HTTP status as an int value
	 * @since 4.3
	 */
	@Override
	public int getStatusCodeValue() {
		return this.statusCode.value();
	}

	@Override
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

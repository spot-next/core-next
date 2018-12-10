package io.spotnext.core.infrastructure.http;

import java.util.Objects;

import spark.ModelAndView;

/**
 * <p>Abstract AbstractResponse class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractResponse extends ModelAndView implements HttpResponse {

	private Object data;
	private HttpStatus httpStatus;

	protected AbstractResponse(HttpStatus httpStatus) {
		super(null, "empty");
		this.httpStatus = httpStatus;
	}
	
	protected AbstractResponse(HttpStatus httpStatus, String viewName) {
		super(null, viewName);
		this.httpStatus = httpStatus;
	}
	
	/** {@inheritDoc} */
	@Override
	public Object getPayload() {
		return data;
	}
	
	/** {@inheritDoc} */
	@Override
	public Object getModel() {
		return getPayload();
	}

	/** {@inheritDoc} */
	@Override
	public <R extends HttpResponse> R withPayload(Object payload) {
		this.data = payload;
		return (R) this;
	}

	/** {@inheritDoc} */
	@Override
	public HttpStatus getHttpStatus() {
		return httpStatus;
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

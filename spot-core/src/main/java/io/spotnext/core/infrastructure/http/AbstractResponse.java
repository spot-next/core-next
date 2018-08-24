package io.spotnext.core.infrastructure.http;

import java.util.Objects;

import spark.ModelAndView;

public abstract class AbstractResponse extends ModelAndView implements HttpResponse {

	private Object payload;
	private HttpStatus httpStatus;

	protected AbstractResponse(HttpStatus httpStatus) {
		super(null, "empty");
		this.httpStatus = httpStatus;
	}
	
	protected AbstractResponse(HttpStatus httpStatus, String viewName) {
		super(null, viewName);
		this.httpStatus = httpStatus;
	}
	
	@Override
	public Object getPayload() {
		return payload;
	}
	
	@Override
	public Object getModel() {
		return getPayload();
	}

	@Override
	public <R extends HttpResponse> R withPayload(Object payload) {
		this.payload = payload;
		return (R) this;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return httpStatus;
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

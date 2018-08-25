package io.spotnext.core.infrastructure.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "model, viewName" })
public class ExceptionResponse extends AbstractResponse {

	private ExceptionResponse(HttpStatus httpStatus, Exception exception) {
		super(httpStatus);
		withPayload(exception);
	}

	public static ExceptionResponse notImplemented(Exception exception) {
		return new ExceptionResponse(HttpStatus.NOT_IMPLEMENTED, exception);
	}

	public static ExceptionResponse badGateway(Exception exception) {
		return new ExceptionResponse(HttpStatus.BAD_GATEWAY, exception);
	}

	public static ExceptionResponse serviceUnavailable(Exception exception) {
		return new ExceptionResponse(HttpStatus.SERVICE_UNAVAILABLE, exception);
	}

	public static ExceptionResponse gatewayTimeout(Exception exception) {
		return new ExceptionResponse(HttpStatus.GATEWAY_TIMEOUT, exception);
	}

	public static ExceptionResponse internalServerError(Exception exception) {
		return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception);
	}

	public static ExceptionResponse withStatus(HttpStatus httpStatus, Exception exception) {
		return new ExceptionResponse(httpStatus, exception);
	}

}

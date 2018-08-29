package io.spotnext.core.infrastructure.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>ExceptionResponse class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@JsonIgnoreProperties(value = { "model, viewName" })
public class ExceptionResponse extends AbstractResponse {

	private ExceptionResponse(HttpStatus httpStatus, Exception exception) {
		super(httpStatus);
		withPayload(exception);
	}

	/**
	 * <p>notImplemented.</p>
	 *
	 * @param exception a {@link java.lang.Exception} object.
	 * @return a {@link io.spotnext.core.infrastructure.http.ExceptionResponse} object.
	 */
	public static ExceptionResponse notImplemented(Exception exception) {
		return new ExceptionResponse(HttpStatus.NOT_IMPLEMENTED, exception);
	}

	/**
	 * <p>badGateway.</p>
	 *
	 * @param exception a {@link java.lang.Exception} object.
	 * @return a {@link io.spotnext.core.infrastructure.http.ExceptionResponse} object.
	 */
	public static ExceptionResponse badGateway(Exception exception) {
		return new ExceptionResponse(HttpStatus.BAD_GATEWAY, exception);
	}

	/**
	 * <p>serviceUnavailable.</p>
	 *
	 * @param exception a {@link java.lang.Exception} object.
	 * @return a {@link io.spotnext.core.infrastructure.http.ExceptionResponse} object.
	 */
	public static ExceptionResponse serviceUnavailable(Exception exception) {
		return new ExceptionResponse(HttpStatus.SERVICE_UNAVAILABLE, exception);
	}

	/**
	 * <p>gatewayTimeout.</p>
	 *
	 * @param exception a {@link java.lang.Exception} object.
	 * @return a {@link io.spotnext.core.infrastructure.http.ExceptionResponse} object.
	 */
	public static ExceptionResponse gatewayTimeout(Exception exception) {
		return new ExceptionResponse(HttpStatus.GATEWAY_TIMEOUT, exception);
	}

	/**
	 * <p>internalServerError.</p>
	 *
	 * @param exception a {@link java.lang.Exception} object.
	 * @return a {@link io.spotnext.core.infrastructure.http.ExceptionResponse} object.
	 */
	public static ExceptionResponse internalServerError(Exception exception) {
		return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception);
	}

	/**
	 * <p>withStatus.</p>
	 *
	 * @param httpStatus a {@link io.spotnext.core.infrastructure.http.HttpStatus} object.
	 * @param exception a {@link java.lang.Exception} object.
	 * @return a {@link io.spotnext.core.infrastructure.http.ExceptionResponse} object.
	 */
	public static ExceptionResponse withStatus(HttpStatus httpStatus, Exception exception) {
		return new ExceptionResponse(httpStatus, exception);
	}

}

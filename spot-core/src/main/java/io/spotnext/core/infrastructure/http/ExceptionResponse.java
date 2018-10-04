package io.spotnext.core.infrastructure.http;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>ExceptionResponse class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@JsonIgnoreProperties(value = { "model, viewName" })
public class ExceptionResponse extends DataResponse {

	private ExceptionResponse(HttpStatus httpStatus, Exception exception) {
		super(httpStatus);

		final String message;

		if (exception instanceof InvocationTargetException) {
			InvocationTargetException ie = (InvocationTargetException) exception;
			message = ie.getTargetException() != null ? ie.getTargetException().getMessage() : exception.getMessage();
		} else {
			message = ExceptionUtils.getRootCauseMessage(exception);
		}

		withError("error." + exception.getClass().getSimpleName().toLowerCase(Locale.getDefault()), message);
	}

	/**
	 * <p>notImplemented.</p>
	 *
	 * @param exception a {@link java.lang.Exception} object.
	 * @return a {@link io.spotnext.infrastructure.http.ExceptionResponse} object.
	 */
	public static ExceptionResponse notImplemented(Exception exception) {
		return new ExceptionResponse(HttpStatus.NOT_IMPLEMENTED, exception);
	}

	/**
	 * <p>badGateway.</p>
	 *
	 * @param exception a {@link java.lang.Exception} object.
	 * @return a {@link io.spotnext.infrastructure.http.ExceptionResponse} object.
	 */
	public static ExceptionResponse badGateway(Exception exception) {
		return new ExceptionResponse(HttpStatus.BAD_GATEWAY, exception);
	}

	/**
	 * <p>serviceUnavailable.</p>
	 *
	 * @param exception a {@link java.lang.Exception} object.
	 * @return a {@link io.spotnext.infrastructure.http.ExceptionResponse} object.
	 */
	public static ExceptionResponse serviceUnavailable(Exception exception) {
		return new ExceptionResponse(HttpStatus.SERVICE_UNAVAILABLE, exception);
	}

	/**
	 * <p>gatewayTimeout.</p>
	 *
	 * @param exception a {@link java.lang.Exception} object.
	 * @return a {@link io.spotnext.infrastructure.http.ExceptionResponse} object.
	 */
	public static ExceptionResponse gatewayTimeout(Exception exception) {
		return new ExceptionResponse(HttpStatus.GATEWAY_TIMEOUT, exception);
	}

	/**
	 * <p>internalServerError.</p>
	 *
	 * @param exception a {@link java.lang.Exception} object.
	 * @return a {@link io.spotnext.infrastructure.http.ExceptionResponse} object.
	 */
	public static ExceptionResponse internalServerError(Exception exception) {
		return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception);
	}

	/**
	 * <p>withStatus.</p>
	 *
	 * @param httpStatus a {@link io.spotnext.infrastructure.http.HttpStatus} object.
	 * @param exception  a {@link java.lang.Exception} object.
	 * @return a {@link io.spotnext.infrastructure.http.ExceptionResponse} object.
	 */
	public static ExceptionResponse withStatus(HttpStatus httpStatus, Exception exception) {
		return new ExceptionResponse(httpStatus, exception);
	}

}

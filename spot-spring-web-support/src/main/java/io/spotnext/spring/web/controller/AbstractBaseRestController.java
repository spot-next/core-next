package io.spotnext.spring.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.spring.web.http.HttpResponse;
import io.spotnext.spring.web.http.Status;

/**
 * <p>Abstract AbstractBaseRestController class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@ControllerAdvice
@SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
public abstract class AbstractBaseRestController extends AbstractBaseController {

	/**
	 * Handles all thrown exceptions and returns error details as JSON.
	 *
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 * @param exception a {@link java.lang.Exception} object.
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 * @return a {@link io.spotnext.spring.web.http.HttpResponse} object.
	 */
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	@ExceptionHandler({ Exception.class, IllegalStateException.class, IllegalArgumentException.class })
	public HttpResponse<Void> handleError(final HttpServletRequest request, final HttpServletResponse response,
			final Exception exception) {

		Logger.exception(String.format("Unhandled exception %s occured: %s",
				exception.getClass().getSimpleName(), exception.getMessage()), exception);

		final HttpResponse<Void> ret = new HttpResponse<>(HttpStatus.INTERNAL_SERVER_ERROR);

		if (exception instanceof HttpMediaTypeNotSupportedException) {
			response.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
		}

		ret.getBody().getErrors().add(new Status("server.error", exception.getMessage()));

		return ret;
	}
}

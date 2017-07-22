package at.spot.spring.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import at.spot.spring.web.dto.Status;
import at.spot.spring.web.http.Response;

public abstract class AbstractBaseRestController extends AbstractBaseController {

	/**
	 * Handles all thrown exceptions and returns error details as JSON.
	 * 
	 * @param request
	 * @param exception
	 * @return
	 */
	@ResponseBody
	@ExceptionHandler(Exception.class)
	public Response<Void> handleError(final HttpServletRequest request, final HttpServletResponse response,
			final Exception exception) {

		loggingService.exception(String.format("Unhandled exception %s occured: %s",
				exception.getClass().getSimpleName(), exception.getMessage()), exception);

		final Response<Void> ret = new Response<>(HttpStatus.INTERNAL_SERVER_ERROR);

		if (exception instanceof HttpMediaTypeNotSupportedException) {
			response.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
		} else {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		ret.getBody().getErrors().add(new Status("server.error", exception.getMessage()));

		return ret;
	}
}

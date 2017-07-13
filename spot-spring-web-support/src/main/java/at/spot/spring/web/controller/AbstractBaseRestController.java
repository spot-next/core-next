package at.spot.spring.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import at.spot.spring.web.dto.Response;
import at.spot.spring.web.dto.Status;

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

		final Response<Void> ret = new Response<>();

		if (exception instanceof HttpMediaTypeNotSupportedException) {
			response.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
		} else {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		ret.getErrors().add(new Status("server.error", exception.getMessage()));

		return ret;
	}
}

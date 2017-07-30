package at.spot.spring.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import at.spot.core.infrastructure.http.HttpResponse;
import at.spot.core.infrastructure.http.Status;

@ControllerAdvice
public abstract class AbstractBaseRestController extends AbstractBaseController {

	/**
	 * Handles all thrown exceptions and returns error details as JSON.
	 * 
	 * @param request
	 * @param exception
	 * @return
	 */
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	@ExceptionHandler({ Exception.class, IllegalStateException.class, IllegalArgumentException.class })
	public HttpResponse<Void> handleError(final HttpServletRequest request, final HttpServletResponse response,
			final Exception exception) {

		loggingService.exception(String.format("Unhandled exception %s occured: %s",
				exception.getClass().getSimpleName(), exception.getMessage()), exception);

		final HttpResponse<Void> ret = new HttpResponse<>(HttpStatus.INTERNAL_SERVER_ERROR);

		if (exception instanceof HttpMediaTypeNotSupportedException) {
			response.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
		}

		ret.getBody().getErrors().add(new Status("server.error", exception.getMessage()));

		return ret;
	}
}

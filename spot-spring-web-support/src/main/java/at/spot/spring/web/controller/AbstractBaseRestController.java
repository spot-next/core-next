package at.spot.spring.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import at.spot.core.infrastructure.service.LoggingService;
import at.spot.spring.web.dto.Response;
import at.spot.spring.web.dto.Status;

@RequestMapping(consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE })
public abstract class AbstractBaseRestController extends AbstractBaseController {

	@Resource
	protected LoggingService loggingService;

	/**
	 * Handles all thrown exceptions and returns error details as JSON.
	 * 
	 * @param request
	 * @param exception
	 * @return
	 */
	@ResponseBody
	@ExceptionHandler(Exception.class)
	@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Response<Void> handleError(final HttpServletRequest request, final HttpServletResponse response,
			final Exception exception) {

		loggingService.error(String.format("Unhandler %s occured: %s", exception.getClass().getSimpleName(),
				exception.getMessage()));

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

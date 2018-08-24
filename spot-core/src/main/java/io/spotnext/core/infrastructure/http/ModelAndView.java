package io.spotnext.core.infrastructure.http;

public class ModelAndView extends AbstractResponse implements ModelAndViewResponse {

	private ModelAndView(HttpStatus httpStatus, String viewName) {
		super(httpStatus, viewName);
	}

	public static ModelAndViewResponse ok(String viewName) {
		final ModelAndViewResponse ret = new ModelAndView(HttpStatus.OK, viewName);
		return ret;
	}

	public static ModelAndViewResponse notFound(String viewName) {
		final ModelAndViewResponse ret = new ModelAndView(HttpStatus.NOT_FOUND, viewName);
		return ret;
	}

	public static ModelAndViewResponse withStatus(HttpStatus httpStatus, String viewName) {
		final ModelAndViewResponse ret = new ModelAndView(httpStatus, viewName);
		return ret;
	}
}

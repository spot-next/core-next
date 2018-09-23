package io.spotnext.core.infrastructure.http;

/**
 * <p>ModelAndView class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ModelAndView extends AbstractResponse implements ModelAndViewResponse {

	private ModelAndView(HttpStatus httpStatus, String viewName) {
		super(httpStatus, viewName);
	}

	/**
	 * <p>ok.</p>
	 *
	 * @param viewName a {@link java.lang.String} object.
	 * @return a {@link io.spotnext.infrastructure.http.ModelAndViewResponse} object.
	 */
	public static ModelAndViewResponse ok(String viewName) {
		final ModelAndViewResponse ret = new ModelAndView(HttpStatus.OK, viewName);
		return ret;
	}

	/**
	 * <p>notFound.</p>
	 *
	 * @param viewName a {@link java.lang.String} object.
	 * @return a {@link io.spotnext.infrastructure.http.ModelAndViewResponse} object.
	 */
	public static ModelAndViewResponse notFound(String viewName) {
		final ModelAndViewResponse ret = new ModelAndView(HttpStatus.NOT_FOUND, viewName);
		return ret;
	}

	/**
	 * <p>withStatus.</p>
	 *
	 * @param httpStatus a {@link io.spotnext.infrastructure.http.HttpStatus} object.
	 * @param viewName a {@link java.lang.String} object.
	 * @return a {@link io.spotnext.infrastructure.http.ModelAndViewResponse} object.
	 */
	public static ModelAndViewResponse withStatus(HttpStatus httpStatus, String viewName) {
		final ModelAndViewResponse ret = new ModelAndView(httpStatus, viewName);
		return ret;
	}
}

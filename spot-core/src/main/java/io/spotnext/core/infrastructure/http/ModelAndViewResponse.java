package io.spotnext.core.infrastructure.http;

/**
 * <p>ModelAndViewResponse interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface ModelAndViewResponse extends HttpResponse {
	/**
	 * <p>getViewName.</p>
	 *
	 * @return the view name
	 */
	public String getViewName();

}

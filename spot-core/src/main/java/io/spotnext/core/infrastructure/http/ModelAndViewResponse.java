package io.spotnext.core.infrastructure.http;

public interface ModelAndViewResponse extends HttpResponse {
	/**
	 * @return the view name
	 */
	public String getViewName();

}

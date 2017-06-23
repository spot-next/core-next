package at.spot.cms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import at.spot.cms.service.CmsPageService;

public abstract class AbstractCmsController {

	@Autowired
	protected CmsPageService cmsPageService;

	/**
	 * Forwards all requests to the {@link CmsPageService} for rendering.
	 * 
	 * @param model
	 * @param request
	 * @param response
	 */
	protected void handleRequest(final HttpServletRequest request, final HttpServletResponse response) {
		cmsPageService.renderRequest(request, response);
	}
}

package at.spot.cms.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.spot.itemtype.cms.model.CmsPage;
import at.spot.itemtype.cms.model.CmsPageTemplate;

public interface CmsPageService {

	CmsPage getPageById(String pageId);

	CmsPageTemplate getPageTemplateById(String pageTemplateId);

	String renderPage(CmsPage page);

	/**
	 * Searches for {@link CmsPage}s that match the given url.
	 * 
	 * @param url
	 *            the request url
	 */
	CmsPage getPageForUrl(String url);

	/**
	 * Searches for a {@link CmsPage} that handles the URL of the request and
	 * renders the page with the configured rendering engine
	 * ({@link CmsPage#getRenderEngine()}):
	 * 
	 * @param request
	 * @param response
	 */
	void renderRequest(HttpServletRequest request, HttpServletResponse response);

}

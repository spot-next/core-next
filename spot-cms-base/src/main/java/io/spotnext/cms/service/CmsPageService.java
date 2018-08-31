package io.spotnext.cms.service;

import io.spotnext.cms.exception.PageNotFoundException;
import io.spotnext.itemtype.cms.CmsPage;

/**
 * <p>
 * CmsPageService interface.
 * </p>
 */
public interface CmsPageService {

	/**
	 * <p>
	 * getPageById.
	 * </p>
	 *
	 * @param pageId a {@link java.lang.String} object.
	 * @return a {@link io.spotnext.itemtype.cms.model.CmsPage} object.
	 * @throws PageNotFoundException
	 */
	CmsPage getPageById(String pageId) throws PageNotFoundException;

	/**
	 * <p>
	 * getPageTemplateById.
	 * </p>
	 *
	 * @param pageTemplateId a {@link java.lang.String} object.
	 * @return a {@link io.spotnext.itemtype.cms.model.CmsPageTemplate} object.
	 */
//	CmsPageTemplate getPageTemplateById(String pageTemplateId);

	/**
	 * <p>
	 * renderPage.
	 * </p>
	 *
	 * @param page a {@link io.spotnext.itemtype.cms.model.CmsPage} object.
	 * @return a {@link java.lang.String} object.
	 */
//	String renderPage(CmsPage page);

	/**
	 * Searches for {@link io.spotnext.itemtype.cms.model.CmsPage}s that match the
	 * given url.
	 *
	 * @param url the request url
	 * @return a {@link io.spotnext.itemtype.cms.model.CmsPage} object.
	 */
//	CmsPage getPageForUrl(String url);

	/**
	 * Searches for a {@link io.spotnext.itemtype.cms.model.CmsPage} that handles
	 * the URL of the request and renders the page with the configured rendering
	 * engine ({@link io.spotnext.itemtype.cms.model.CmsPage#getRenderEngine()}):
	 *
	 * @param request  a {@link javax.servlet.http.HttpServletRequest} object.
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 */
//	void renderRequest(HttpServletRequest request, HttpServletResponse response);

}

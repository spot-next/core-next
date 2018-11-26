package io.spotnext.cms.endpoints;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import io.spotnext.cms.exception.PageNotFoundException;
import io.spotnext.cms.service.TemplateRenderService;
import io.spotnext.core.infrastructure.http.ModelAndView;
import io.spotnext.itemtype.cms.CmsPage;

/**
 * The abstract base class class for HTTP endpoints that will be rendered using
 * a {@link CmsPage}.
 */
public abstract class AbstractPageEndpoint {

	@Autowired
	protected TemplateRenderService templateRenderService;

	/**
	 * Prepares a {@link ModelAndView} based on the given cms page.
	 * 
	 * @param pageId  the id og the {@link CmsPage} to use
	 * @param context contains the variables for the template render service - can
	 *                be null
	 * @return the prepared {@link ModelAndView} for the template renderer
	 * @return the prepared {@link ModelAndView} that will be rendered
	 */
	protected ModelAndView renderCmsPage(String pageId, Map<String, Object> context) throws PageNotFoundException {
		return templateRenderService.prepareCmsPage(pageId, context);
	}

}

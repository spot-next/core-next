package io.spotnext.cms.service;

import java.util.Map;

import io.spotnext.cms.exception.PageNotFoundException;
import io.spotnext.cms.exception.TemplateRenderException;
import io.spotnext.cms.strategy.TemplateRenderStrategy;
import io.spotnext.core.infrastructure.http.ModelAndView;
import io.spotnext.itemtype.cms.CmsPage;
import io.spotnext.itemtype.cms.enumeration.TemplateRenderEngine;

/**
 * <p>
 * TemplateRenderService interface.
 * </p>
 */
public interface TemplateRenderService {
	/**
	 * Renders the given context object into the template (the concrete
	 * {@link io.spotnext.cms.strategy.TemplateRenderStrategy} implementation
	 * resolves the template using the templateName parameter).
	 *
	 * @param engine       the template engine to use, determins which
	 *                     {@link io.spotnext.cms.strategy.TemplateRenderStrategy}
	 *                     to will render the template.
	 * @param templateName the name the
	 *                     {@link io.spotnext.cms.strategy.TemplateRenderStrategy}
	 *                     will use to find the actual template file.
	 * @param context      the context object that holds the data that will be
	 *                     rendered into the template.
	 * @return the rendered string output
	 * @throws TemplateRenderException in case no suitable
	 *                                 {@link TemplateRenderStrategy} is found
	 */
	String renderTemplate(TemplateRenderEngine engine, String templateName, Object context)
			throws TemplateRenderException;

	/**
	 * Prepares a {@link ModelAndView} instance for the {@link CmsPage}. The context
	 * will be merged in the context generated based on the {@link CmsPage}.
	 * 
	 * @param pageId  of the {@link CmsPage} item
	 * @param context custom variables for rendering
	 * @return the prepared {@link ModelAndView} for the template renderer
	 * @throws PageNotFoundException   in case the page with the given id does not
	 *                                 exist
	 * @throws TemplateRenderException in case no suitable
	 *                                 {@link TemplateRenderStrategy} is found
	 */
	ModelAndView prepareCmsPage(String pageId, Map<String, Object> context)
			throws PageNotFoundException, TemplateRenderException;

}

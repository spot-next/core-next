package io.spotnext.cms.strategy;

import java.util.Map;

import io.spotnext.cms.exception.PageNotFoundException;
import io.spotnext.core.infrastructure.http.ModelAndView;
import io.spotnext.itemtype.cms.CmsPage;
import io.spotnext.itemtype.cms.enumeration.TemplateRenderEngine;

/**
 * <p>
 * TemplateRenderStrategy interface.
 * </p>
 */
public interface TemplateRenderStrategy {
	/**
	 * Renders the given context object into the template (the concrete
	 * {@link io.spotnext.cms.strategy.TemplateRenderStrategy} implementation
	 * resolves the template using the templateName parameter).
	 *
	 * @param templateName the name the
	 *                     {@link io.spotnext.cms.strategy.TemplateRenderStrategy}
	 *                     will use to find the actual template file.
	 * @param context      the context object that holds the data that will be
	 *                     rendered into the template.
	 * @return the rendered string output
	 */
	String renderTemplate(String templateName, Object context);

	/**
	 * Prepares a {@link ModelAndView} instance for the {@link CmsPage}. The context
	 * will be merged in the context generated based on the {@link CmsPage}.
	 * 
	 * @param page    the {@link CmsPage} to be used for rendering
	 * @param context custom variables for rendering
	 * @return the prepared {@link ModelAndView} for the template renderer
	 * @throws PageNotFoundException in case the page with the given id does not
	 *                               exist
	 */
	ModelAndView prepareCmsPage(CmsPage page, Map<String, Object> context) throws PageNotFoundException;

	/**
	 * @return returns the supported {@link TemplateRenderEngine}.
	 */
	TemplateRenderEngine supportsEngine();

}

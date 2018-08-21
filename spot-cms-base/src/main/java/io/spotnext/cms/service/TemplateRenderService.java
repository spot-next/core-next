package io.spotnext.cms.service;

import io.spotnext.cms.strategy.TemplateRenderStrategy;
import io.spotnext.itemtype.cms.enumeration.TemplateRenderEngine;

public interface TemplateRenderService {
	/**
	 * Renders the given context object into the template (the concrete
	 * {@link TemplateRenderStrategy} implementation resolves the template using the
	 * templateName parameter).
	 * 
	 * @param engine       the template engine to use, determins which
	 *                     {@link TemplateRenderStrategy} to will render the
	 *                     template.
	 * @param templateName the name the {@link TemplateRenderStrategy} will use to
	 *                     find the actual template file.
	 * @param context      the context object that holds the data that will be
	 *                     rendered into the template.
	 * @return the rendered string output
	 */
	String renderTemplate(TemplateRenderEngine engine, String templateName, Object context);
}

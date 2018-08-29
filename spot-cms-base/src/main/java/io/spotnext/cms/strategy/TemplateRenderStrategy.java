package io.spotnext.cms.strategy;

/**
 * <p>TemplateRenderStrategy interface.</p>
 */
public interface TemplateRenderStrategy {
	/**
	 * Renders the given context object into the template (the concrete
	 * {@link io.spotnext.cms.strategy.TemplateRenderStrategy} implementation resolves the template using the
	 * templateName parameter).
	 *
	 * @param templateName the name the {@link io.spotnext.cms.strategy.TemplateRenderStrategy} will use to
	 *                     find the actual template file.
	 * @param context      the context object that holds the data that will be
	 *                     rendered into the template.
	 * @return the rendered string output
	 */
	String renderTemplate(String templateName, Object context);
}

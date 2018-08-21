package io.spotnext.cms.strategy;

public interface TemplateRenderStrategy {
	/**
	 * Renders the given context object into the template (the concrete
	 * {@link TemplateRenderStrategy} implementation resolves the template using the
	 * templateName parameter).
	 * 
	 * @param templateName the name the {@link TemplateRenderStrategy} will use to
	 *                     find the actual template file.
	 * @param context      the context object that holds the data that will be
	 *                     rendered into the template.
	 * @return the rendered string output
	 */
	String renderTemplate(String templateName, Object context);
}

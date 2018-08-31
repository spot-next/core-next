package io.spotnext.cms.endpoints;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import io.spotnext.cms.annotations.Renderable;
import io.spotnext.cms.exception.PageNotFoundException;
import io.spotnext.cms.service.CmsPageService;
import io.spotnext.core.infrastructure.http.ModelAndView;
import io.spotnext.itemtype.cms.CmsPage;

/**
 * The abstract base class class for HTTP endpoints that will be rendered using
 * a {@link CmsPage}.
 */
public abstract class AbstractPageEndpoint {

	@Resource
	protected CmsPageService cmsPageService;

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
		final CmsPage page = cmsPageService.getPageById(pageId);

		// create a new context and put both the page item (converted to a map, and the
		// given context) into it
		final Map<String, Object> mergedContext = new HashMap<>();

		// first include the layout renderable properties of the page
		mergedContext.putAll(page.getLayout().getProperties((f, v) -> f.isAnnotationPresent(Renderable.class)));

		// then let the page properties overwrite the layout properties, if not null!
		mergedContext.putAll(page.getProperties((f, v) -> f.isAnnotationPresent(Renderable.class) && v != null));

		// finally add the given context
		if (context != null) {
			mergedContext.putAll(context);
		}

		// every template has to to have a "page" variable that contains the actual page
		// to render.
		mergedContext.put("pageTemplate", page.getRenderTemplateFile());

		return renderTemplatePage(page.getLayout().getRenderTemplateFile(), page.getRenderTemplateFile(),
				mergedContext);
	}

	/**
	 * Prepares a {@link ModelAndView} for the template renderer based on the given
	 * layout, page and context.
	 * 
	 * @param layout  the layout template to use, must be a path like
	 *                "template/layouts/layout"
	 * @param page    the (content) page template to use, same path as the layout
	 *                parameter
	 * @param context the context holding the variables for the template renderer
	 * @return the prepared {@link ModelAndView} that will be rendered
	 */
	protected ModelAndView renderTemplatePage(String layout, String page, Map<String, Object> context) {
		context.put("page", page);

		return ModelAndView.ok(layout).withPayload(context);
	}

}

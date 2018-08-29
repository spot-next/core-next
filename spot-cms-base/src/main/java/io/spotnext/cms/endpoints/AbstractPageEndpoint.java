package io.spotnext.cms.endpoints;

import java.util.Map;

import io.spotnext.core.infrastructure.http.ModelAndView;
import io.spotnext.itemtype.cms.model.CmsPage;

/**
 * The abstract base class class for HTTP endpoints that will be rendered using
 * a {@link CmsPage}.
 */
public abstract class AbstractPageEndpoint {

	protected ModelAndView renderPage(String layout, String page, Map<String, Object> context) {
		context.put("page", page);

		return ModelAndView.ok(layout).withPayload(context);
	}
}

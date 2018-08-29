package io.spotnext.cms.rendering.provider;

import javax.servlet.http.HttpServletRequest;

import io.spotnext.cms.rendering.view.View;
import io.spotnext.itemtype.cms.model.CmsPage;

/**
 * <p>Abstract AbstractRenderContextProvider class.</p>
 */
public abstract class AbstractRenderContextProvider {

	/**
	 * <p>render.</p>
	 *
	 * @param page a {@link io.spotnext.itemtype.cms.model.CmsPage} object.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 * @return a {@link io.spotnext.cms.rendering.view.View} object.
	 */
	public abstract View render(CmsPage page, HttpServletRequest request);
}

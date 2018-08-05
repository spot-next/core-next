package io.spotnext.cms.rendering.provider;

import javax.servlet.http.HttpServletRequest;

import io.spotnext.cms.rendering.view.View;
import io.spotnext.itemtype.cms.model.CmsPage;

public abstract class AbstractRenderContextProvider {

	public abstract View render(CmsPage page, HttpServletRequest request);
}

package at.spot.cms.rendering.provider;

import javax.servlet.http.HttpServletRequest;

import at.spot.cms.model.CmsPage;
import at.spot.cms.rendering.view.View;

public abstract class AbstractRenderContextProvider {

	public abstract View render(CmsPage page, HttpServletRequest request);
}

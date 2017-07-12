package at.spot.cms.rendering.provider;

import javax.servlet.http.HttpServletRequest;

import at.spot.cms.rendering.view.View;
import at.spot.itemtype.cms.model.CmsPage;

public abstract class AbstractRenderContextProvider {

	public abstract View render(CmsPage page, HttpServletRequest request);
}

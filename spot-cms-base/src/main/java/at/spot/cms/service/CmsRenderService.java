package at.spot.cms.service;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

import at.spot.cms.rendering.view.View;

public interface CmsRenderService {

	/**
	 * Renders the given view into the outputstream.
	 * 
	 * @param view
	 * @param request
	 * @param outputStream
	 */
	void renderView(View view, HttpServletRequest request, OutputStream outputStream);
}

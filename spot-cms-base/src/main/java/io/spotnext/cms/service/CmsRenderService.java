package io.spotnext.cms.service;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

import io.spotnext.cms.rendering.view.View;

/**
 * <p>CmsRenderService interface.</p>
 */
public interface CmsRenderService {

	/**
	 * Renders the given view into the outputstream.
	 *
	 * @param view a {@link io.spotnext.cms.rendering.view.View} object.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 * @param outputStream a {@link java.io.OutputStream} object.
	 */
	void renderView(View view, HttpServletRequest request, OutputStream outputStream);
}

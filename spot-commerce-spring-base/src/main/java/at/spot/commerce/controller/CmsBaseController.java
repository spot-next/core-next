package at.spot.commerce.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import at.spot.cms.controller.AbstractCmsController;
import at.spot.cms.service.CmsPageService;

/**
 * This controller handles all requests and forwards them to the
 * {@link CmsPageService}.
 */
@Controller(value = "/*")
public class CmsBaseController extends AbstractCmsController {

	@RequestMapping(method = RequestMethod.GET)
	public void handleGet(final HttpServletRequest request, final HttpServletResponse response) {

		handleRequest(request, response);
	}

	@RequestMapping(method = RequestMethod.POST)
	public void handlePost(final HttpServletRequest request, final HttpServletResponse response) {

		handleRequest(request, response);
	}
}

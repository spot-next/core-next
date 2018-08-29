package io.spotnext.commerce.controller;

import org.springframework.stereotype.Controller;

import io.spotnext.cms.controller.AbstractCmsEndpoint;
import io.spotnext.cms.service.CmsPageService;

/**
 * This controller handles all requests and forwards them to the
 * {@link CmsPageService}.
 */
@Controller(value = "/*")
public class CmsBaseEndpoint extends AbstractCmsEndpoint {

}

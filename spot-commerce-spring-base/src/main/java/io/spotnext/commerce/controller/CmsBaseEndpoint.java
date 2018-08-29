package io.spotnext.commerce.controller;

import org.springframework.stereotype.Controller;

import io.spotnext.cms.endpoints.AbstractPageEndpoint;
import io.spotnext.cms.service.CmsPageService;

/**
 * This controller handles all requests and forwards them to the
 * {@link CmsPageService}.
 */
@Controller(value = "/*")
public class CmsBaseEndpoint extends AbstractPageEndpoint {

}

package io.spotnext.cms.controller;

import org.springframework.beans.factory.annotation.Autowired;

import io.spotnext.cms.service.CmsPageService;
import io.spotnext.itemtype.cms.model.CmsPage;

/**
 * The abstract base class class for HTTP endpoints that will be rendered using
 * a {@link CmsPage}.
 */
public abstract class AbstractCmsEndpoint {

	@Autowired
	protected CmsPageService cmsPageService;

}

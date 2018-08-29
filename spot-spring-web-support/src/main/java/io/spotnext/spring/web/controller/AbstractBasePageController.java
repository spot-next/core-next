package io.spotnext.spring.web.controller;

import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * <p>Abstract AbstractBasePageController class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractBasePageController extends AbstractBaseController {

	@ModelAttribute(name = "pageTitle")
	protected abstract String getPageTitle();

}

package at.spot.spring.web.controller;

import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class AbstractBasePageController extends AbstractBaseController {

	@ModelAttribute(name = "pageTitle")
	protected abstract String getPageTitle();

}

package at.spot.spring.web.controller;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.WebApplicationContext;

import at.spot.core.infrastructure.service.LoggingService;

public abstract class AbstractBaseController implements ApplicationContextAware {

	private WebApplicationContext webContext;

	@Autowired
	protected LoggingService loggingService;

	@ModelAttribute(name = "pageTitle")
	protected abstract String getPageTitle();

	@Override
	public void setApplicationContext(final ApplicationContext context) throws BeansException {
		this.webContext = (WebApplicationContext) context;
	}

	public WebApplicationContext getWebApplicationContext() {
		return this.webContext;
	}
}

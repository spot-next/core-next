package io.spotnext.cms.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

import io.spotnext.cms.service.TemplateRenderService;
import io.spotnext.cms.strategy.impl.ThymeleafTemplateRenderStrategy;
import io.spotnext.itemtype.cms.enumeration.TemplateRenderEngine;

@Service
public class DefaultTemplateRenderService implements TemplateRenderService {

	@Resource
	private ThymeleafTemplateRenderStrategy thymeleafTemplateRenderStrategy;

	@Override
	public String renderTemplate(TemplateRenderEngine engine, String templateName, Object context) {
		if (TemplateRenderEngine.THYMELEAF.equals(engine)) {
			return thymeleafTemplateRenderStrategy.renderTemplate(templateName, context);
		} else {
			throw new NotImplementedException();
		}
	}

}

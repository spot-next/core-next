package io.spotnext.cms.rendering.transformers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spotnext.cms.service.TemplateRenderService;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.core.support.util.ValidationUtil;
import io.spotnext.itemtype.cms.enumeration.TemplateRenderEngine;
import spark.ModelAndView;
import spark.ResponseTransformer;

/**
 * Renders the given object (has to be an instance of {@link ModelAndView})
 * using thymeleaf rendering engine.
 */
@Service
public class ThymeleafRendererResponseTransformer implements ResponseTransformer {

	@Autowired
	protected TemplateRenderService templateRenderService;

	@Override
	public String render(final Object object) throws Exception {
		ValidationUtil.validateEquals("Only instances of ModelAndView can be rendered", object instanceof ModelAndView);
		ModelAndView viewModel = (ModelAndView) object;

		return getTemplateRenderService().renderTemplate(TemplateRenderEngine.THYMELEAF, viewModel.getViewName(),
				viewModel);
	}

	protected TemplateRenderService getTemplateRenderService() {
		if (templateRenderService == null)
			templateRenderService = Registry.getApplicationContext().getBean("templateRenderService",
					TemplateRenderService.class);

		return templateRenderService;
	}
}

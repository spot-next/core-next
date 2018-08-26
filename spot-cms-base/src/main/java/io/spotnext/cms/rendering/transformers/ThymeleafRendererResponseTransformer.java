package io.spotnext.cms.rendering.transformers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spotnext.cms.service.TemplateRenderService;
import io.spotnext.core.infrastructure.http.HttpResponse;
import io.spotnext.core.infrastructure.http.HttpStatus;
import io.spotnext.core.infrastructure.support.HttpRequestHolder;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.core.management.transformer.ResponseTransformer;
import io.spotnext.core.support.util.ValidationUtil;
import io.spotnext.itemtype.cms.enumeration.TemplateRenderEngine;
import spark.ModelAndView;
import spark.Request;

/**
 * Renders the given object (has to be an instance of {@link ModelAndView})
 * using thymeleaf rendering engine.
 */
@Service
public class ThymeleafRendererResponseTransformer implements ResponseTransformer {

	@Autowired
	protected TemplateRenderService templateRenderService;

	@Override
	public String handleResponse(final Object object) throws Exception {
		ValidationUtil.validateEquals("Only instances of ModelAndView can be rendered", object instanceof ModelAndView);
		ModelAndView viewModel = (ModelAndView) object;

		return getTemplateRenderService().renderTemplate(TemplateRenderEngine.THYMELEAF, viewModel.getViewName(),
				viewModel);
	}

	@Override
	public String handleException(final Object object, Exception exception) throws Exception {
		Map<String, Object> model = new HashMap<>();
		model.put("exceptionSimpleName", exception.getClass().getSimpleName());
		model.put("exceptionName", exception.getClass().getName());
		model.put("message", exception.getMessage());
		model.put("stackTrace", ExceptionUtils.getStackTrace(exception));
		model.put("timestamp", LocalDateTime.now());
		
		Request currentRequest = HttpRequestHolder.getRequest();
		model.put("urlPath", currentRequest.url());
		
		if (object instanceof HttpResponse) {
			HttpStatus status = ((HttpResponse) object).getHttpStatus();
			model.put("httpStatusCode", status.value());
			model.put("httpStatusName", status.name());
		}
		
		final ModelAndView ret = new ModelAndView(model, "exception");
		
		return handleResponse(ret);
	}

	protected TemplateRenderService getTemplateRenderService() {
		if (templateRenderService == null)
			templateRenderService = Registry.getApplicationContext().getBean("templateRenderService",
					TemplateRenderService.class);

		return templateRenderService;
	}
}
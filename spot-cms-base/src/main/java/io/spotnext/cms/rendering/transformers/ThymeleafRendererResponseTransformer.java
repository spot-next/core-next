package io.spotnext.cms.rendering.transformers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

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
import spark.Session;

/**
 * Renders the given object (has to be an instance of
 * {@link spark.ModelAndView}) using thymeleaf rendering engine.
 */
@Service
public class ThymeleafRendererResponseTransformer implements ResponseTransformer {

	@Autowired
	protected TemplateRenderService templateRenderService;

	/** {@inheritDoc} */
	@Override
	public String handleResponse(final Object object) throws Exception {
		ValidationUtil.validateTrue("Only instances of ModelAndView can be rendered", object instanceof ModelAndView);
		final ModelAndView viewModel = (ModelAndView) object;

		return getTemplateRenderService().renderTemplate(TemplateRenderEngine.THYMELEAF, viewModel.getViewName(), viewModel);
	}

	/** {@inheritDoc} */
	@Override
	public String handleException(final Object object, final Exception exception) throws Exception {
		final Map<String, Object> model = new HashMap<>();
		model.put("exceptionSimpleName", exception.getClass().getSimpleName());
		model.put("exceptionName", exception.getClass().getName());
		model.put("message", exception.getMessage());
		model.put("stackTrace", ExceptionUtils.getStackTrace(exception));
		model.put("timestamp", LocalDateTime.now());

		final Request currentRequest = HttpRequestHolder.getRequest();
		model.put("urlPath", currentRequest.url());
		model.put("request", currentRequest);
		model.put("attributes", getAttributes(currentRequest, currentRequest::attributes, currentRequest::attribute));
		final Session session = currentRequest.session();
		model.put("session", getAttributes(currentRequest, session::attributes, session::attribute));

		if (object instanceof HttpResponse) {
			final HttpStatus status = ((HttpResponse) object).getHttpStatus();
			model.put("httpStatusCode", status.value());
			model.put("httpStatusName", status.name());
			model.put("request", object);
		}

		final ModelAndView ret = new ModelAndView(model, "exception");

		return handleResponse(ret);
	}

	protected Map<String, Object> getAttributes(final Request request, final Supplier<Set<String>> keySupplier, final Function<String, Object> valueSupplier) {
		final Map<String, Object> params = new HashMap<>();

		for (final String key : keySupplier.get()) {
			params.put(key, valueSupplier.apply(key));
		}

		return params;
	}

	/**
	 * <p>
	 * Getter for the field <code>templateRenderService</code>.
	 * </p>
	 *
	 * @return a {@link io.spotnext.cms.service.TemplateRenderService} object.
	 */
	protected TemplateRenderService getTemplateRenderService() {
		if (templateRenderService == null)
			templateRenderService = Registry.getApplicationContext().getBean("templateRenderService", TemplateRenderService.class);

		return templateRenderService;
	}
}

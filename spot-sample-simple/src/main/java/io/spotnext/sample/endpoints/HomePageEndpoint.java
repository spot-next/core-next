package io.spotnext.sample.endpoints;

import java.util.HashMap;
import java.util.Map;

import io.spotnext.cms.rendering.transformers.ThymeleafRendererResponseTransformer;
import io.spotnext.core.infrastructure.support.MimeType;
import io.spotnext.core.management.annotation.Handler;
import io.spotnext.core.management.annotation.RemoteEndpoint;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

@RemoteEndpoint(pathMapping = "/")
public class HomePageEndpoint {

	@Handler(responseTransformer = ThymeleafRendererResponseTransformer.class, mimeType = MimeType.HTML)
	public ModelAndView get(final Request request, final Response response) {
		final Map<String, Object> model = new HashMap<>();

		model.put("name", "user");

		return new ModelAndView(model, "homepage");
	}
}

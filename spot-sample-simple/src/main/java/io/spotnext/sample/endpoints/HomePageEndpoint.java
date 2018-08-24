package io.spotnext.sample.endpoints;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import io.spotnext.cms.rendering.transformers.ThymeleafRendererResponseTransformer;
import io.spotnext.core.infrastructure.http.ModelAndView;
import io.spotnext.core.infrastructure.support.MimeType;
import io.spotnext.core.management.annotation.Handler;
import io.spotnext.core.management.annotation.RemoteEndpoint;
import io.spotnext.core.persistence.query.JpqlQuery;
import io.spotnext.core.persistence.query.QueryResult;
import io.spotnext.core.persistence.service.QueryService;
import io.spotnext.sample.types.itemtypes.Party;
import spark.Request;
import spark.Response;

@RemoteEndpoint(pathMapping = "/")
public class HomePageEndpoint {

	@Resource
	private QueryService queryService;

	@Handler(responseTransformer = ThymeleafRendererResponseTransformer.class, mimeType = MimeType.HTML)
	public ModelAndView get(final Request request, final Response response) {
		final Map<String, Object> model = new HashMap<>();

		model.put("pageTitle", "Party service sample page");
		model.put("parties", getAllParties());

		return ModelAndView.ok("homepage").withPayload(model);
	}

	protected List<Party> getAllParties() {
		String query = "SELECT p FROM Party p";
		JpqlQuery<Party> partyQuery = new JpqlQuery<>(query, Party.class);
		QueryResult<Party> result = queryService.query(partyQuery);

		return result.getResultList();
	}
}

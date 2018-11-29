package io.spotnext.sample.endpoints;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.cms.rendering.transformers.ThymeleafRendererResponseTransformer;
import io.spotnext.core.infrastructure.http.ModelAndView;
import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.infrastructure.service.UserService;
import io.spotnext.core.infrastructure.support.MimeType;
import io.spotnext.core.management.annotation.Handler;
import io.spotnext.core.management.annotation.RemoteEndpoint;
import io.spotnext.core.persistence.query.JpqlQuery;
import io.spotnext.core.persistence.query.QueryResult;
import io.spotnext.core.persistence.service.QueryService;
import io.spotnext.core.security.service.AuthenticationService;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;
import io.spotnext.sample.filters.IsAdminFilter;
import io.spotnext.sample.types.party.Party;
import spark.Request;
import spark.Response;
import spark.route.HttpMethod;

@RemoteEndpoint(pathMapping = "/")
public class HomePageEndpoint {

	@Autowired
	private ModelService modelService;

	@Autowired
	private QueryService queryService;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private UserService<User, UserGroup> userService;

	@Handler(responseTransformer = ThymeleafRendererResponseTransformer.class, mimeType = MimeType.HTML)
	public ModelAndView getHomepage(final Request request, final Response response) {
		final Map<String, Object> model = new HashMap<>();

		model.put("pageTitle", "Party service sample page");
		model.put("parties", getAllParties());

		return ModelAndView.ok("homepage").withPayload(model);
	}

	@Handler(responseTransformer = ThymeleafRendererResponseTransformer.class, pathMapping = "/login", mimeType = MimeType.HTML, method = HttpMethod.post)
	public ModelAndView postLogin(final Request request, final Response response) {
		String username = request.queryParams("username");
		String password = request.queryParams("password");

		if (username != null && password != null) {
			User user = authenticationService.getAuthenticatedUser(username, password, false);

			if (user != null) {
				userService.setCurrentUser(user);
				response.redirect("/manage");
				return null;
			}
		}

		response.redirect("/");
		return null;
	}

	@Handler(responseTransformer = ThymeleafRendererResponseTransformer.class, pathMapping = "/logout", mimeType = MimeType.HTML, method = HttpMethod.post)
	public ModelAndView postLogout(final Request request, final Response response) {
		userService.setCurrentUser(null);
		response.redirect("/");
		return null;
	}

	@Handler(responseTransformer = ThymeleafRendererResponseTransformer.class, pathMapping = "/manage", mimeType = MimeType.HTML, authenticationFilter = IsAdminFilter.class)
	public ModelAndView getManage(final Request request, final Response response) {
		final Map<String, Object> model = new HashMap<>();
		model.put("pageTitle", "Party service sample page");
		model.put("parties", getAllParties());

		model.put("isLoggedIn", true);

		return ModelAndView.ok("homepage").withPayload(model);
	}

	@SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
	@Handler(responseTransformer = ThymeleafRendererResponseTransformer.class, pathMapping = "/cancel", mimeType = MimeType.HTML, method = HttpMethod.post, authenticationFilter = IsAdminFilter.class)
	public ModelAndView postCancelParty(final Request request, final Response response) {
		String partyId = request.queryParams("partyId");

		if (partyId != null) {
			modelService.remove(Party.class, Long.valueOf(partyId));
		}

		response.redirect("/manage");
		return null;
	}

	protected List<Party> getAllParties() {
		String query = "SELECT p FROM Party p";
		JpqlQuery<Party> partyQuery = new JpqlQuery<>(query, Party.class);
		QueryResult<Party> result = queryService.query(partyQuery);

		return result.getResultList();
	}
}
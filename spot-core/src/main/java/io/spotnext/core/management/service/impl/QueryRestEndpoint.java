package io.spotnext.core.management.service.impl;

import java.util.Locale;
import java.util.Map;

import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;

import io.spotnext.core.constant.CoreConstants;
//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.annotation.logging.Log;
import io.spotnext.core.infrastructure.http.DataResponse;
import io.spotnext.core.infrastructure.http.HttpResponse;
import io.spotnext.core.infrastructure.support.LogLevel;
import io.spotnext.core.infrastructure.support.MimeType;
import io.spotnext.core.management.annotation.Handler;
import io.spotnext.core.management.annotation.RemoteEndpoint;
import io.spotnext.core.management.support.BasicAuthenticationFilter;
import io.spotnext.core.management.support.data.PageablePayload;
import io.spotnext.core.management.transformer.JsonResponseTransformer;
import io.spotnext.core.persistence.query.JpqlQuery;
import io.spotnext.core.persistence.query.Pageable;
import io.spotnext.core.persistence.query.QueryResult;
import io.spotnext.core.persistence.service.QueryService;
import io.spotnext.support.util.MiscUtil;
import io.spotnext.support.util.ValidationUtil;
import spark.Request;
import spark.Response;
import spark.route.HttpMethod;

/**
 * The /model REST endpoint.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@RemoteEndpoint(portConfigKey = "service.typesystem.rest.port", port = 19000, pathMapping = "/v1/query", authenticationFilter = BasicAuthenticationFilter.class)
public class QueryRestEndpoint extends AbstractRestEndpoint {

	@Autowired
	protected QueryService queryService;

	/**
	 * Gets all items of the given item type. The page index starts at 1.
	 *
	 * @param          <T> a T object.
	 * @param request  a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Handler(method = HttpMethod.get, pathMapping = "", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Object> HttpResponse query(final Request request, final Response response) {

		final int page = MiscUtil.intOrDefault(request.queryParams("page"), CoreConstants.REQUEST_DEFAULT_PAGE);
		final int pageSize = MiscUtil.intOrDefault(request.queryParams("pageSize"), CoreConstants.REQUEST_DEFAULT_PAGE_SIZE);

		String queryString = request.queryParams("q");
		String resultTypeString = request.queryParams("resultType");

		final Class<T> resultType;

		if (resultTypeString != null && "map".equals(resultTypeString.toLowerCase(Locale.getDefault()))) {
			resultType = (Class<T>) Map.class;
		} else {
			resultType = (Class<T>) Tuple.class;
		}

		try {
			ValidationUtil.validateNotEmpty("Query string must be provided using ?q=", queryString);

			final JpqlQuery<T> query = new JpqlQuery<T>(queryString, resultType);
			query.setPage(page);
			query.setPageSize(pageSize);

			// allow only queries, not inserts or updates to protect from SQL injections
			query.setReadOnly(true);

			// important to avoid the N+1 problem
			query.setEagerFetchRelations(true);
			final QueryResult<T> results = queryService.query(query);

			final Pageable<T> pageableData = new PageablePayload<>(results.getResults(), results.getPage(), results.getPageSize(),
					results.getTotalCount());

			return DataResponse.ok().withPayload(pageableData);
		} catch (final Exception e) {
			return handleGenericException(e);
		}
	}

}

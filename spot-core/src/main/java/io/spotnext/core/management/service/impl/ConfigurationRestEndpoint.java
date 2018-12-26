package io.spotnext.core.management.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import io.spotnext.core.constant.CoreConstants;
//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.annotation.logging.Log;
import io.spotnext.core.infrastructure.http.DataResponse;
import io.spotnext.core.infrastructure.http.HttpResponse;
import io.spotnext.core.infrastructure.service.ConfigurationService;
import io.spotnext.core.infrastructure.support.LogLevel;
import io.spotnext.core.infrastructure.support.MimeType;
import io.spotnext.core.management.annotation.Handler;
import io.spotnext.core.management.annotation.RemoteEndpoint;
import io.spotnext.core.management.support.BasicAuthenticationFilter;
import io.spotnext.core.management.support.data.PageablePayload;
import io.spotnext.core.management.transformer.JsonResponseTransformer;
import io.spotnext.core.persistence.query.Pageable;
import io.spotnext.support.util.MiscUtil;
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
@RemoteEndpoint(portConfigKey = "service.typesystem.rest.port", port = 19000, pathMapping = "/v1/configuration", authenticationFilter = BasicAuthenticationFilter.class)
public class ConfigurationRestEndpoint extends AbstractRestEndpoint {

	@Autowired
	protected ConfigurationService configurationService;

	/**
	 * Returns all the configuration properties starting with the given prefix.
	 *
	 * @param request  a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Handler(method = HttpMethod.get, pathMapping = { "", "/",
			"/:propertyPrefix" }, mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public HttpResponse getPropertyWithPrefix(final Request request, final Response response) {
		return DataResponse.ok().withPayload(getPaginatedProperties(request));
	}

	/**
	 * Creates or updates a configuration property.
	 *
	 * @param request  a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Handler(method = HttpMethod.put, pathMapping = "/:property", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public HttpResponse setProperty(final Request request, final Response response) {

		final String property = request.params(":property");
		final String value = request.body();

		if (StringUtils.isNotBlank(property)) {
			configurationService.setProperty(property, value);

			return DataResponse.ok();
		} else {
			return DataResponse.badRequest().withError("error.rest.configuration.update.", "Property cannot be empty");
		}
	}

	/**
	 * Returns the configuration properties with the given prefix.
	 * 
	 * @param request containing the pagination settings and property key
	 * @return the the configuration entries, never null
	 */
	private Pageable<Entry<Object, Object>> getPaginatedProperties(final Request request) {
		final int page = MiscUtil.positiveIntOrDefault(request.queryParams("page"), CoreConstants.REQUEST_DEFAULT_PAGE);
		final int pageSize = MiscUtil.intOrDefault(request.queryParams("pageSize"), CoreConstants.REQUEST_DEFAULT_PAGE_SIZE);
		final String propertyPrefix = StringUtils.defaultString(request.params(":propertyPrefix"), "");

		final Properties properties = configurationService.getProperties(propertyPrefix);

		final List<Entry<Object, Object>> entries = properties.entrySet().stream() //
				.skip(MiscUtil.positiveIntOrDefault(page - 1, 1) * pageSize) //
				.limit(pageSize) //
				.sorted(new Comparator<Entry<Object, Object>>() {
					@Override
					public int compare(Entry<Object, Object> o1, Entry<Object, Object> o2) {
						if (o1.getKey() instanceof String && o2.getKey() instanceof String) {
							return ((String) o1.getKey()).compareTo((String) o2.getKey());
						}

						throw new IllegalStateException("Can only compare property keys of type 'String'");
					}
				}) //
				.collect(Collectors.toList());

		final var pageableData = new PageablePayload<Entry<Object, Object>>(entries, page, pageSize, (long) entries.size());

		return pageableData;
	}
}

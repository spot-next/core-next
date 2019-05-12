package io.spotnext.core.management.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import io.spotnext.core.infrastructure.annotation.logging.Log;
import io.spotnext.core.infrastructure.http.DataResponse;
import io.spotnext.core.infrastructure.http.ExceptionResponse;
import io.spotnext.core.infrastructure.http.HttpResponse;
import io.spotnext.core.infrastructure.scheduling.service.impl.CronJobService;
import io.spotnext.core.infrastructure.support.LogLevel;
import io.spotnext.core.infrastructure.support.MimeType;
import io.spotnext.core.management.annotation.Handler;
import io.spotnext.core.management.annotation.RemoteEndpoint;
import io.spotnext.core.management.support.BasicAuthenticationFilter;
import io.spotnext.core.management.transformer.JsonResponseTransformer;
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
@RemoteEndpoint(portConfigKey = "service.cronjob.rest.port", port = 19000, pathMapping = "/v1/cronjob/", authenticationFilter = BasicAuthenticationFilter.class)
public class CronJobEndpoint extends AbstractRestEndpoint {

	@Autowired
	protected CronJobService cronJobService;

	/**
	 * Provides type system actions to initialize, update and clear the database schema
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Handler(method = HttpMethod.get, pathMapping = { "/" }, mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public HttpResponse listCronJobs(final Request request, final Response response) {
		final var cronjobs = cronJobService.getAllCronJobs();
		return DataResponse.ok().withPayload(cronjobs);
	}

	/**
	 * Provides type system actions to initialize, update and clear the database schema
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Handler(method = HttpMethod.get, pathMapping = { "/start/:cronJobUid" }, mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public HttpResponse startCronJob(final Request request, final Response response) {
		String cronJobUid = request.params(":cronJobUid");

		try {
			cronJobService.startCronJob(cronJobUid);
			return DataResponse.ok();
		} catch (Exception e) {
			return ExceptionResponse.badRequest().withError("error.cronjob.startjob", e.getMessage());
		}
	}

	/**
	 * Provides type system actions to initialize, update and clear the database schema
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Handler(method = HttpMethod.get, pathMapping = { "/abort/:cronJobUid" }, mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public HttpResponse abortCronJob(final Request request, final Response response) {
		String cronJobUid = request.params(":cronJobUid");

		try {
			cronJobService.abortCronJob(cronJobUid);
			return DataResponse.ok();
		} catch (Exception e) {
			return ExceptionResponse.badRequest().withError("error.cronjob.startjob", e.getMessage());
		}
	}
}

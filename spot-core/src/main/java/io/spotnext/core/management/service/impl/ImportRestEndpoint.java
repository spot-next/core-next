package io.spotnext.core.management.service.impl;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonProperty;

//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.annotation.logging.Log;
import io.spotnext.core.infrastructure.http.DataResponse;
import io.spotnext.core.infrastructure.http.HttpResponse;
import io.spotnext.core.infrastructure.service.ImportService;
import io.spotnext.core.infrastructure.support.LogLevel;
import io.spotnext.core.infrastructure.support.MimeType;
import io.spotnext.core.management.annotation.Handler;
import io.spotnext.core.management.annotation.RemoteEndpoint;
import io.spotnext.core.management.support.BasicAuthenticationFilter;
import io.spotnext.core.management.transformer.JsonResponseTransformer;
import io.spotnext.itemtype.core.beans.ImportConfiguration;
import io.spotnext.itemtype.core.beans.SerializationConfiguration;
import io.spotnext.itemtype.core.enumeration.DataFormat;
import io.spotnext.support.util.ValidationUtil;
import spark.Request;
import spark.Response;
import spark.route.HttpMethod;

/**
 * The /import REST endpoint.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@RemoteEndpoint(portConfigKey = "service.typesystem.rest.port", port = 19000, pathMapping = "/v1/import", authenticationFilter = BasicAuthenticationFilter.class)
public class ImportRestEndpoint extends AbstractRestEndpoint {

	private static final SerializationConfiguration SERIALIZATION_CONFIG = new SerializationConfiguration();
	static {
		SERIALIZATION_CONFIG.setFormat(DataFormat.JSON);
	}

	@Autowired
	protected ImportService importService;

	/**
	 * Gets all items of the given item type. The page index starts at 1.
	 *
	 * @param          <T> a T object.
	 * @param request  a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Handler(method = HttpMethod.post, pathMapping = "", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Object> HttpResponse executeImport(final Request request, final Response response) {
		final String script = request.body();

		if (StringUtils.isNotBlank(script)) {
			try {
				final ImportRequest importRequest = serializationService.deserialize(SERIALIZATION_CONFIG, script, ImportRequest.class);

				ValidationUtil.validateNotNull("Import config missing", importRequest.getConfig());
				ValidationUtil.validateNotBlankOrEmpty("Import script missing", importRequest.getScript());

				if (StringUtils.isBlank(importRequest.getConfig().getScriptIdentifier())) {
					importRequest.getConfig().setScriptIdentifier("HTTP request from " + request.ip());
				}

				importService.importItems(importRequest.getConfig(), new ByteArrayInputStream(importRequest.getScript().getBytes(StandardCharsets.UTF_8)));

				return DataResponse.accepted();
			} catch (final Exception e) {
				return handleGenericException(e);
			}
		} else {
			return DataResponse.badRequest().withError("import.nocontent", "Request body must be supplied");
		}
	}

	public static class ImportRequest {
		@JsonProperty
		private ImportConfiguration config;
		@JsonProperty
		private String[] script;

		public ImportRequest() {
			// necessary for deserialization
		}

		public ImportRequest(ImportConfiguration config, String[] script) {
			this.config = config;
			this.script = Arrays.copyOf(script, script.length);
		}

		public ImportConfiguration getConfig() {
			return config;
		}

		public String getScript() {
			return StringUtils.join(script, "\n");
		}
	}
}

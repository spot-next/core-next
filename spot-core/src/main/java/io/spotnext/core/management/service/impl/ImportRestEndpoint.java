package io.spotnext.core.management.service.impl;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.sun.management.OperatingSystemMXBean;

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
import io.spotnext.itemtype.core.beans.SerializationConfiguration;
import io.spotnext.itemtype.core.enumeration.DataFormat;
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
@RemoteEndpoint(portConfigKey = "service.typesystem.rest.port", port = 19000, pathMapping = "/v1/status/", authenticationFilter = BasicAuthenticationFilter.class)
public class ImportRestEndpoint extends AbstractRestEndpoint {

	private ThreadLocal<NumberFormat> formatMegaByte = ThreadLocal.withInitial(() -> new DecimalFormat("0 MB"));
	private ThreadLocal<NumberFormat> formatPercent = ThreadLocal.withInitial(() -> new DecimalFormat("0%"));

	private final OperatingSystemMXBean osMX = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	private final Runtime runtime = Runtime.getRuntime();

	@Autowired
	protected ImportService importService;

	private static final SerializationConfiguration SERIALIZATION_CONFIG = new SerializationConfiguration();
	static {
		SERIALIZATION_CONFIG.setFormat(DataFormat.JSON);
	}

	/**
	 * Returns all available status information.
	 *
	 * @param          <T> a T object.
	 * @param request  a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the status message
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Handler(pathMapping = "", method = HttpMethod.get, mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Object> HttpResponse status(final Request request, final Response response) {
		Map<String, Object> status = new HashMap<>();
		status.put("cpu", cpuStatus());
		status.put("memory", memoryStatus());

		return DataResponse.ok().withPayload(status);
	}

	/**
	 * Returns information about the currently use memory.
	 *
	 * @param          <T> a T object.
	 * @param request  a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the status message
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Handler(pathMapping = "/memory", method = HttpMethod.get, mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Object> HttpResponse memory(final Request request, final Response response) {
		return DataResponse.ok().withPayload(memoryStatus());
	}

	/**
	 * Returns information about the currently use cpu usage.
	 *
	 * @param          <T> a T object.
	 * @param request  a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the status message
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Handler(pathMapping = "/cpu", method = HttpMethod.get, mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Object> HttpResponse cpu(final Request request, final Response response) {

		return DataResponse.ok().withPayload(cpuStatus());
	}

	protected MemoryStatus memoryStatus() {
		final long freeMemory = runtime.freeMemory();
		final long maxMemory = runtime.maxMemory();
		final long allocatedMemory = runtime.totalMemory();
		final long totalFreeMemory = freeMemory + (maxMemory - allocatedMemory);

		final MemoryStatus status = new MemoryStatus();
		status.freeMemory = formatAsMB(freeMemory);
		status.maxMemory = formatAsMB(maxMemory);
		status.allocatedMemory = formatAsMB(allocatedMemory);
		status.totalFreeMemory = formatAsMB(totalFreeMemory);
		return status;
	}

	protected CpuStatus cpuStatus() {
		final CpuStatus status = new CpuStatus();
		status.processCpuLoad = formatPercent.get().format(osMX.getProcessCpuLoad());

		return status;
	}

	private String formatAsMB(long value) {
		return formatMegaByte.get().format(value / 1024 / 1024);
	}

	protected static class CpuStatus {
		String processCpuLoad;
	}

	protected static class MemoryStatus {
		String maxMemory;
		String allocatedMemory;
		String freeMemory;
		String totalFreeMemory;
	}

}

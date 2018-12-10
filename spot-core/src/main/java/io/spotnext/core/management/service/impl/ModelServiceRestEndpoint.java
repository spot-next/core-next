package io.spotnext.core.management.service.impl;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;

import io.spotnext.core.constant.CoreConstants;
//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.annotation.logging.Log;
import io.spotnext.core.infrastructure.exception.DeserializationException;
import io.spotnext.core.infrastructure.exception.ModelNotFoundException;
import io.spotnext.core.infrastructure.exception.ModelSaveException;
import io.spotnext.core.infrastructure.exception.ModelValidationException;
import io.spotnext.core.infrastructure.exception.UnknownTypeException;
import io.spotnext.core.infrastructure.http.DataResponse;
import io.spotnext.core.infrastructure.http.HttpResponse;
import io.spotnext.core.infrastructure.http.HttpStatus;
import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.infrastructure.support.HttpHeader;
import io.spotnext.core.infrastructure.support.LogLevel;
import io.spotnext.core.infrastructure.support.MimeType;
import io.spotnext.core.management.annotation.Handler;
import io.spotnext.core.management.annotation.RemoteEndpoint;
import io.spotnext.core.management.support.BasicAuthenticationFilter;
import io.spotnext.core.management.support.data.PageablePayload;
import io.spotnext.core.management.transformer.JsonResponseTransformer;
import io.spotnext.core.persistence.exception.ModelNotUniqueException;
import io.spotnext.core.persistence.exception.QueryException;
import io.spotnext.core.persistence.query.JpqlQuery;
import io.spotnext.core.persistence.query.ModelQuery;
import io.spotnext.core.persistence.query.Pageable;
import io.spotnext.core.persistence.query.Queries;
import io.spotnext.core.persistence.query.QueryResult;
import io.spotnext.core.persistence.query.SortOrder;
import io.spotnext.core.persistence.service.QueryService;
import io.spotnext.infrastructure.type.Item;
import io.spotnext.itemtype.core.beans.SerializationConfiguration;
import io.spotnext.itemtype.core.enumeration.DataFormat;
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
@RemoteEndpoint(portConfigKey = "service.typesystem.rest.port", port = 19000, pathMapping = "/v1/models", authenticationFilter = BasicAuthenticationFilter.class)
public class ModelServiceRestEndpoint extends AbstractRestEndpoint {

	private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("ddd, dd MM yy hh:mm:ss z");

	@Autowired
	protected ModelService modelService;

	@Autowired
	protected QueryService queryService;

	private static final SerializationConfiguration SERIALIZATION_CONFIG = new SerializationConfiguration();
	static {
		SERIALIZATION_CONFIG.setFormat(DataFormat.JSON);
	}

	/**
	 * Gets all items of the given item type. The page index starts at 1.
	 *
	 * @param <T> a T object.
	 * @param request a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Handler(method = HttpMethod.head, pathMapping = "/:typecode", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse getModelsInfo(final Request request, final Response response) {

		try {
			final String typeCode = request.params(":typecode");
			final Class<T> type = (Class<T>) typeService.getClassForTypeCode(typeCode);

			final QueryResult<Long> modelCount = queryService.query(Queries.countAll(type));

			response.header("Item-Count", modelCount.getResults().get(0) + "");

			return DataResponse.ok();
		} catch (final UnknownTypeException e) {
			return RESPONSE_UNKNOWN_TYPE;
		} catch (final Exception e) {
			return handleGenericException(e);
		}
	}

	/**
	 * Gets all items of the given item type. The page index starts at 1.
	 *
	 * @param <T> a T object.
	 * @param request a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Handler(method = HttpMethod.head, pathMapping = "/:typecode/:id", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse getModelInfo(final Request request, final Response response) {

		try {
			final String typeCode = request.params(":typecode");
			final Class<T> type = (Class<T>) typeService.getClassForTypeCode(typeCode);
			final long id = MiscUtil.longOrDefault(request.params(":id"), -1);

			final JpqlQuery<ModelInfoData> query = new JpqlQuery<>(
					String.format("SELECT i.id AS id, i.version As version, i.lastModifiedAt As lastModifiedAt FROM %s AS i WHERE i.id = :id",
							type.getSimpleName()),
					ModelInfoData.class);
			query.addParam("id", id);
			final QueryResult<ModelInfoData> model = queryService.query(query);

			if (model.getTotalCount() > 0) {
				final ModelInfoData info = model.getResults().get(0);

				setCachingHeaderFields(info.getVersion(), info.getLastModifiedAt(), response);
				return DataResponse.ok();
			} else {
				return DataResponse.notFound();
			}
		} catch (final UnknownTypeException e) {
			return RESPONSE_UNKNOWN_TYPE;
		} catch (final Exception e) {
			return handleGenericException(e);
		}
	}

	/**
	 * Gets all items of the given item type. The page index starts at 1.
	 *
	 * @param <T> a T object.
	 * @param request a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Handler(method = HttpMethod.get, pathMapping = "/:typecode", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse getModels(final Request request, final Response response) {

		final String typeCode = request.params(":typecode");
		final Class<T> type;
		try {
			type = (Class<T>) typeService.getClassForTypeCode(typeCode);
		} catch (final UnknownTypeException e) {
			return RESPONSE_UNKNOWN_TYPE;
		}

		final String[] queryParamValues = request.queryParamsValues("q");
		final int page = MiscUtil.intOrDefault(request.queryParams("page"), CoreConstants.REQUEST_DEFAULT_PAGE);
		final int pageSize = MiscUtil.intOrDefault(request.queryParams("pageSize"), CoreConstants.REQUEST_DEFAULT_PAGE_SIZE);

		String orderByClause = getOrderByClause(request);
		orderByClause = StringUtils.isNotBlank(orderByClause) ? "ORDER BY " + orderByClause : "";

		final JpqlQuery<T> query = ArrayUtils.isNotEmpty(queryParamValues) ? //
				new JpqlQuery<>(String.format("SELECT x FROM %s x WHERE %s %s", type.getSimpleName(), queryParamValues[0], orderByClause), type) : //
				Queries.selectAll(type, getOrderByClause(request));
		query.setEagerFetchRelations(true);
		query.setPage(page);
		query.setPageSize(pageSize);
		// important to avoid the N+1 problem
		query.setEagerFetchRelations(true);

		try {
			final QueryResult<T> result = queryService.query(query);

			final Pageable<T> pageableData = new PageablePayload<>(result.getResults(), result.getPage(), result.getPageSize(),
					result.getTotalCount());

			if (pageableData.getTotalCount() > 0) {
				return DataResponse.ok().withPayload(pageableData);
			} else {
				return DataResponse.notFound().withPayload(pageableData);
			}
		} catch (final QueryException e) {
			return DataResponse.withStatus(HttpStatus.BAD_REQUEST).withError("error.query.execution",
					"Cannot execute given query: " + e.getMessage());
		} catch (final Exception e) {
			return handleGenericException(e);
		}
	}

	/**
	 * Gets an item based on the ID.
	 *
	 * @param <T> a T object.
	 * @param request a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Handler(method = HttpMethod.get, pathMapping = "/:typecode/:id", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse getModel(final Request request, final Response response) {

		final String typeCode = request.params(":typecode");
		final long id = MiscUtil.longOrDefault(request.params(":id"), -1);

		try {
			if (id > 0) {
				final Class<T> type = (Class<T>) typeService.getClassForTypeCode(typeCode);
				final ModelQuery<T> query = new ModelQuery<>(type, Collections.singletonMap("id", id));
				query.setEagerFetchRelations(true);
				final T model = modelService.get(query);

				if (model == null) {
					return DataResponse.notFound();
				}

				final Date lastModified = Date.from(model.getLastModifiedAt().toInstant(ZoneOffset.UTC));
				setCachingHeaderFields(model.getVersion(), lastModified, response);

				return DataResponse.ok().withPayload(model);
			} else {
				return DataResponse.withStatus(HttpStatus.BAD_REQUEST).withError("error.onget",
						"No valid ID provided.");
			}
		} catch (final ModelNotFoundException e) {
			return DataResponse.withStatus(HttpStatus.BAD_REQUEST).withError("error.ongetall", "Item not found.");
		} catch (final UnknownTypeException e) {
			return RESPONSE_UNKNOWN_TYPE;
		} catch (final Exception e) {
			return handleGenericException(e);
		}
	}

	/**
	 * <p>
	 * Gets item based on an example.
	 * </p>
	 * {@link ModelService#get(Class, Map)} is called (= search by example).
	 *
	 * @param <T> a T object.
	 * @param request a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	// @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
	@Handler(method = HttpMethod.post, pathMapping = "/:typecode/query/", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse queryModelByExample(final Request request, final Response response) {

		try {
			final T example = deserializeToItem(request);
			final List<T> items = modelService.getAllByExample(example);

			if (items.size() > 0) {
				return DataResponse.ok().withPayload(items);
			} else {
				return DataResponse.notFound().withPayload(items);
			}
		} catch (final UnknownTypeException e) {
			return RESPONSE_UNKNOWN_TYPE;
		} catch (final DeserializationException e) {
			return DataResponse.withStatus(HttpStatus.BAD_REQUEST).withError("query.unknowntype",
					"Could not deserialize request body into valid example item.");
		} catch (final Exception e) {
			return handleGenericException(e);
		}
	}

	/**
	 * Creates a new item. If the item is not unique (based on its unique properties), an error is returned.
	 *
	 * @param <T> a T object.
	 * @param request a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	// @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
	@Handler(method = HttpMethod.post, pathMapping = "/:typecode", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse createModel(final Request request, final Response response) {

		try {
			final T item = deserializeToItem(request);
			modelService.save(item);

			// convert to string! see ItemSerializationMixIn
			return DataResponse.created().withPayload(Collections.singletonMap("id", item.getId() + ""));
		} catch (final UnknownTypeException e) {
			return RESPONSE_UNKNOWN_TYPE;
		} catch (final DeserializationException e) {
			return DataResponse.withStatus(HttpStatus.BAD_REQUEST).withError("error.oncreate", e.getMessage());
		} catch (final ModelNotUniqueException | ModelSaveException e) {
			return DataResponse.withStatus(HttpStatus.CONFLICT).withError("error.model.notunique", String.format(
					"Another item with the same uniqueness criteria (but a different ID) was found: %s", e.getMessage()));
		} catch (final ModelValidationException e) {
			final List<String> messages = new ArrayList<>();
			messages.add(e.getMessage());

			e.getConstraintViolations().stream().map((c) -> {
				return String.format("%s.%s could not be set to {%s}: %s", c.getRootBean().getClass().getSimpleName(),
						c.getPropertyPath(), c.getInvalidValue(), c.getMessage());
			}).forEach(m -> messages.add(m));

			return DataResponse.withStatus(HttpStatus.CONFLICT).withError("error.model.validation",
					String.join("\n", messages));
		} catch (final Exception e) {
			return handleGenericException(e);
		}
	}

	/**
	 * Removes the given item. The ID or a search criteria has to be set.
	 *
	 * @param <T> a T object.
	 * @param request a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Handler(method = HttpMethod.delete, pathMapping = "/:typecode/:id", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse deleteModel(final Request request, final Response response) {

		final String typeCode = request.params(":typecode");
		final long id = MiscUtil.longOrDefault(request.params(":id"), -1);

		if (id > -1) {
			final Class<T> type;
			try {
				type = (Class<T>) typeService.getClassForTypeCode(typeCode);
			} catch (final UnknownTypeException e) {
				return RESPONSE_UNKNOWN_TYPE;
			}
			try {
				modelService.remove(type, id);

				return DataResponse.withStatus(HttpStatus.ACCEPTED);
			} catch (final ModelNotFoundException e) {
				return DataResponse.notFound().withError("error.ondelete", "Item with given ID not found.");
			} catch (final Exception e) {
				return handleGenericException(e);
			}
		} else {
			return DataResponse.withStatus(HttpStatus.PRECONDITION_FAILED).withError("error.ondelete",
					"No valid ID given.");
		}
	}

	/**
	 * Updates an existing or creates the item with the given values. The ID must be provided. If the new item is not unique, an error is returned.<br/>
	 * Attention: fields that are omitted will be treated as @null. If you just want to update a few fields, use the PATCH Method.
	 *
	 * @param <T> a T object.
	 * @param request a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Handler(method = HttpMethod.put, pathMapping = "/:typecode", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse createOrUpdateModel(final Request request, final Response response) {
		try {
			final JSONObject jsonBody = new JSONObject(request.body());

			if (jsonBody.has("id")) {
				// jsonBody.getLong doesn't work correctly, because the java long is bigger than the javascript number type
				return partiallyUpdateModel(request, NumberUtils.toLong(jsonBody.getString("id")));
			} else {
				return createModel(request, response);
			}

		} catch (final JSONException e) {
			return DataResponse.withStatus(HttpStatus.PRECONDITION_FAILED).withError("error.onpartialupdate",
					"Could not deserialize body json content.");
		}
	}

	/**
	 * Update an existing model with the given values. If the item with the given ID doesn't not exist, an exception is thrown.
	 *
	 * @param <T> a T object.
	 * @param request a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Handler(method = HttpMethod.patch, pathMapping = "/:typecode/:id", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse partiallyUpdateModel(final Request request, final Response response) {

		final long id = MiscUtil.longOrDefault(request.params(":id"), -1);

		if (id > 0) {
			return partiallyUpdateModel(request, id);
		} else {
			return DataResponse.withStatus(HttpStatus.BAD_REQUEST).withError("error.onpatch", "No valid ID provided.");
		}
	}

	protected <T extends Item> HttpResponse partiallyUpdateModel(final Request request, final long id) {
		// get type
		final String typeCode = request.params(":typecode");
		final Class<T> type;
		try {
			type = (Class<T>) typeService.getClassForTypeCode(typeCode);
		} catch (final UnknownTypeException e) {
			return RESPONSE_UNKNOWN_TYPE;
		}

		try {
			// search old item
			T oldItem = modelService.get(type, id);

			if (oldItem == null) {
				throw new ModelNotFoundException(String.format("Item with ID=%s not found", id));
			}

			// get body as json object
			oldItem = deserializeToItem(request, oldItem);
			oldItem.markAsDirty();

			modelService.save(oldItem);

			return DataResponse.accepted();
		} catch (final UnknownTypeException e) {
			return RESPONSE_UNKNOWN_TYPE;
		} catch (final ModelNotUniqueException | ModelValidationException e) {
			return DataResponse.conflict().withError("error.onpartialupdate", e.getMessage());
		} catch (final ModelNotFoundException e) {
			return DataResponse.notFound().withError("error.onpartialupdate",
					"No item with the given ID found to update.");
		} catch (final DeserializationException e) {
			return DataResponse.withStatus(HttpStatus.PRECONDITION_FAILED).withError("error.onpartialupdate",
					"Could not deserialize body json content.");
		} catch (final Exception e) {
			return handleGenericException(e);
		}
	}

	private String getOrderByClause(final Request request) {
		return StringUtils.defaultString(request.queryParams("sort"), "".intern());
	}

	private void setOrderBy(final Request request, final ModelQuery<?> query) {
		final String orderBy = getOrderByClause(request);

		if (StringUtils.isNotBlank(orderBy)) {
			final String[] parts = StringUtils.split(orderBy, ",");

			if (parts.length > 0) {
				for (final String part : parts) {
					query.addOrderBy(SortOrder.of(part));
				}
			}
		}
	}

	protected <T extends Item> T deserializeToItem(final Request request)
			throws DeserializationException, UnknownTypeException {
		return deserializeToItem(request, null);
	}

	protected <T extends Item> T deserializeToItem(final Request request, final T objectToUpdate)
			throws DeserializationException, UnknownTypeException {

		final String typeCode = request.params(":typecode");
		final Class<T> type = (Class<T>) typeService.getClassForTypeCode(typeCode);

		final T item;

		if (objectToUpdate != null) {
			item = serializationService.deserialize(SERIALIZATION_CONFIG, request.body(), objectToUpdate);
		} else {
			item = serializationService.deserialize(SERIALIZATION_CONFIG, request.body(), type);
		}

		if (item == null) {
			throw new DeserializationException("Request body was empty");
		}

		return item;
	}

	protected JsonNode deserializeToJsonToken(final Request request)
			throws UnknownTypeException, DeserializationException {
		final String content = request.body();

		return serializationService.deserialize(SERIALIZATION_CONFIG, content, JsonNode.class);
	}

	private void setCachingHeaderFields(final int version, final Date lastModifiedDate, final Response response) {
		// uses the entity version as ETag (for caching)
		response.header(HttpHeader.ETag.toString(), version + "");
		response.header(HttpHeader.LastModified.toString(), DATE_FORMAT.format(lastModifiedDate));
	}

	private static DataResponse RESPONSE_UNKNOWN_TYPE = DataResponse.withStatus(HttpStatus.BAD_REQUEST)
			.withError("error.ongetall", "Unknown item type.");

	private static class ModelInfoData {
		private long id;
		private int version;
		private Date lastModifiedAt;

		public long getId() {
			return id;
		}

		public int getVersion() {
			return version;
		}

		public Date getLastModifiedAt() {
			return lastModifiedAt;
		}
	}
}

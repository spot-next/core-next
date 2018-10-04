package io.spotnext.core.management.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.exception.DeserializationException;
import io.spotnext.core.infrastructure.exception.ModelNotFoundException;
import io.spotnext.core.infrastructure.exception.ModelSaveException;
import io.spotnext.core.infrastructure.exception.ModelValidationException;
import io.spotnext.core.infrastructure.exception.UnknownTypeException;
import io.spotnext.core.infrastructure.http.DataResponse;
import io.spotnext.core.infrastructure.http.HttpResponse;
import io.spotnext.core.infrastructure.http.HttpStatus;
import io.spotnext.core.infrastructure.service.ModelService;
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
import io.spotnext.core.persistence.query.QueryResult;
import io.spotnext.core.persistence.service.QueryService;
import io.spotnext.infrastructure.type.Item;
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

	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_PAGE_SIZE = 100;

	@Autowired
	protected ModelService modelService;

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
	@Handler(method = HttpMethod.head, pathMapping = "/:typecode", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse getModelsInfo(final Request request, final Response response) {

		try {
			final String typeCode = request.params(":typecode");
			final Class<T> type = (Class<T>) typeService.getClassForTypeCode(typeCode);

			final JpqlQuery<Long> query = new JpqlQuery<>(String.format("SELECT count(i) FROM %s AS i", type.getSimpleName()), Long.class);
			final QueryResult<Long> modelCount = queryService.query(query);

			response.header("Item-Count", modelCount.getResultList().get(0) + "");

			return DataResponse.ok();
		} catch (final UnknownTypeException e) {
			return RESPONSE_UNKNOWN_TYPE;
		}
	}

	/**
	 * Gets all items of the given item type. The page index starts at 1.
	 *
	 * @param          <T> a T object.
	 * @param request  a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@Handler(method = HttpMethod.head, pathMapping = "/:typecode/:pk", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse getModelInfo(final Request request, final Response response) {

		try {
			final String typeCode = request.params(":typecode");
			final Class<T> type = (Class<T>) typeService.getClassForTypeCode(typeCode);
			final long pk = MiscUtil.longOrDefault(request.params(":pk"), -1);

			final JpqlQuery<Long> query = new JpqlQuery<>(String.format("SELECT i.pk FROM %s AS i WHERE i.pk = :pk", type.getSimpleName()), Long.class);
			query.addParam("pk", pk);
			final QueryResult<Long> modelCount = queryService.query(query);

			if (modelCount.getResultCount() > 0) {
				return DataResponse.ok();
			} else {
				return DataResponse.notFound();
			}
		} catch (final UnknownTypeException e) {
			return RESPONSE_UNKNOWN_TYPE;
		}
	}

	/**
	 * Gets all items of the given item type. The page index starts at 1.
	 *
	 * @param          <T> a T object.
	 * @param request  a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@Handler(method = HttpMethod.get, pathMapping = "/:typecode", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse getModels(final Request request, final Response response) {

		final int page = MiscUtil.intOrDefault(request.queryParams("page"), DEFAULT_PAGE);
		final int pageSize = MiscUtil.intOrDefault(request.queryParams("pageSize"), DEFAULT_PAGE_SIZE);

		try {
			final Class<T> type = (Class<T>) typeService.getClassForTypeCode(request.params(":typecode"));
			final ModelQuery<T> query = new ModelQuery<>(type, null);
			query.setPage(page);
			query.setPageSize(pageSize);

			// important to avoid the N+1 problem
			query.setEagerFetchRelations(true);
			final List<T> models = modelService.getAll(query);

			final PageablePayload<T> pageableData = new PageablePayload<>(models, page, pageSize);

			return DataResponse.ok().withPayload(pageableData);
		} catch (final UnknownTypeException e) {
			return RESPONSE_UNKNOWN_TYPE;
		}
	}

	/**
	 * Gets an item based on the PK.
	 *
	 * @param          <T> a T object.
	 * @param request  a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@Handler(method = HttpMethod.get, pathMapping = "/:typecode/:pk", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> DataResponse getModel(final Request request, final Response response) {

		final String typeCode = request.params(":typecode");
		final long pk = MiscUtil.longOrDefault(request.params(":pk"), -1);

		try {
			if (pk > 0) {
				final Class<T> type = (Class<T>) typeService.getClassForTypeCode(typeCode);
				final T model = modelService.get(type, pk);

				if (model == null) {
					return DataResponse.notFound();
				}

				return DataResponse.ok().withPayload(model);
			} else {
				return DataResponse.withStatus(HttpStatus.BAD_REQUEST).withError("error.onget",
						"No valid PK provided.");
			}
		} catch (final ModelNotFoundException e) {
			return DataResponse.withStatus(HttpStatus.BAD_REQUEST).withError("error.ongetall", "Item not found.");
		} catch (final UnknownTypeException e) {
			return RESPONSE_UNKNOWN_TYPE;
		}
	}

	/**
	 * Gets an item based on the search query. The query is a JPQL WHERE clause.<br />
	 * Example: .../country/query?q=isoCode = 'CZ' AND isoCode NOT LIKE 'A%' <br/>
	 *
	 * @param          <T> a T object.
	 * @param request  a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@Handler(method = HttpMethod.get, pathMapping = "/:typecode/query/", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> DataResponse queryModel(final Request request, final Response response) {

		final String typeCode = request.params(":typecode");
		final Class<T> type;
		try {
			type = (Class<T>) typeService.getClassForTypeCode(typeCode);
		} catch (final UnknownTypeException e) {
			return RESPONSE_UNKNOWN_TYPE;
		}

		final String[] queryParamValues = request.queryParamsValues("q");
		final int page = MiscUtil.intOrDefault(request.queryParams("page"), DEFAULT_PAGE);
		final int pageSize = MiscUtil.intOrDefault(request.queryParams("pageSize"), DEFAULT_PAGE_SIZE);

		if (ArrayUtils.isNotEmpty(queryParamValues)) {
			final JpqlQuery<T> query = new JpqlQuery<>(
					String.format("SELECT x FROM %s x WHERE %s", type.getSimpleName(), queryParamValues[0]), type);
			query.setEagerFetchRelations(true);
			query.setPage(page);
			query.setPageSize(pageSize);

			try {
				final QueryResult<T> result = queryService.query(query);
				
				if (result.getResultCount() > 0) {
					return DataResponse.ok().withPayload(result);
				} else {
					return DataResponse.notFound().withPayload(result);
				}
			} catch (final QueryException e) {
				return DataResponse.withStatus(HttpStatus.BAD_REQUEST).withError("error.query.execution",
						"Cannot execute given query: " + e.getMessage());
			}
		} else {
			return DataResponse.withStatus(HttpStatus.PRECONDITION_FAILED).withError("query.error",
					"Query could not be parsed.");
		}
	}

	/**
	 * <p>
	 * Gets item based on an example.
	 * </p>
	 * {@link ModelService#get(Class, Map)} is called (= search by example).
	 *
	 * @param          <T> a T object.
	 * @param request  a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
	@Handler(method = HttpMethod.post, pathMapping = "/:typecode/query/", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> DataResponse queryModelByExample(final Request request, final Response response) {

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
		}
	}

	/**
	 * Creates a new item. If the item is not unique (based on its unique properties), an error is returned.
	 *
	 * @param          <T> a T object.
	 * @param request  a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
	@Handler(method = HttpMethod.post, pathMapping = "/:typecode", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> DataResponse createModel(final Request request, final Response response) {

		try {
			final T item = deserializeToItem(request);
			modelService.save(item);

			return DataResponse.created().withPayload(Collections.singletonMap("pk", item.getPk()));
		} catch (final UnknownTypeException e) {
			return RESPONSE_UNKNOWN_TYPE;
		} catch (final DeserializationException e) {
			return DataResponse.withStatus(HttpStatus.BAD_REQUEST).withError("error.oncreate", e.getMessage());
		} catch (final ModelNotUniqueException | ModelSaveException e) {
			return DataResponse.withStatus(HttpStatus.CONFLICT).withError("error.model.notunique", String.format(
					"Another item with the same uniqueness criteria (but a different PK) was found: %s", e.getMessage()));
		} catch (final ModelValidationException e) {
			final List<String> messages = new ArrayList<>();
			messages.add(e.getMessage());

			e.getConstraintViolations().stream().map((c) -> {
				return String.format("%s.%s could not be set to {%s}: %s", c.getRootBean().getClass().getSimpleName(),
						c.getPropertyPath(), c.getInvalidValue(), c.getMessage());
			}).forEach(m -> messages.add(m));

			return DataResponse.withStatus(HttpStatus.CONFLICT).withError("error.model.validation",
					String.join("\n", messages));
		}
	}

	/**
	 * Removes the given item. The PK or a search criteria has to be set.
	 *
	 * @param          <T> a T object.
	 * @param request  a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@Handler(method = HttpMethod.delete, pathMapping = "/:typecode/:pk", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> DataResponse deleteModel(final Request request, final Response response) {

		final String typeCode = request.params(":typecode");
		final long pk = MiscUtil.longOrDefault(request.params(":pk"), -1);

		if (pk > -1) {
			final Class<T> type;
			try {
				type = (Class<T>) typeService.getClassForTypeCode(typeCode);
			} catch (final UnknownTypeException e) {
				return RESPONSE_UNKNOWN_TYPE;
			}
			try {
				modelService.remove(type, pk);

				return DataResponse.withStatus(HttpStatus.ACCEPTED);
			} catch (final ModelNotFoundException e) {
				return DataResponse.notFound().withError("error.ondelete", "Item with given PK not found.");
			}
		} else {
			return DataResponse.withStatus(HttpStatus.PRECONDITION_FAILED).withError("error.ondelete",
					"No valid PK given.");
		}
	}

	/**
	 * Updates an existing or creates the item with the given values. The PK must be provided. If the new item is not unique, an error is returned.<br/>
	 * Attention: fields that are omitted will be treated as @null. If you just want to update a few fields, use the PATCH Method.
	 *
	 * @param          <T> a T object.
	 * @param request  a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@Handler(method = HttpMethod.put, pathMapping = "/:typecode", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> DataResponse createOrUpdateModel(final Request request, final Response response) {
		try {
			final JSONObject jsonBody = new JSONObject(request.body());

			if (jsonBody.has("pk")) {
				return partiallyUpdateModel(request, jsonBody.getLong("pk"));
			} else {
				return createModel(request, response);
			}

		} catch (final JSONException e) {
			return DataResponse.withStatus(HttpStatus.PRECONDITION_FAILED).withError("error.onpartialupdate",
					"Could not deserialize body json content.");
		}
	}

	/**
	 * Update an existing model with the given values. If the item with the given PK doesn't not exist, an exception is thrown.
	 *
	 * @param          <T> a T object.
	 * @param request  a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return the response object
	 */
	@Handler(method = HttpMethod.patch, pathMapping = "/:typecode/:pk", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> DataResponse partiallyUpdateModel(final Request request, final Response response) {

		final long pk = MiscUtil.longOrDefault(request.params(":pk"), -1);

		if (pk > 0) {
			return partiallyUpdateModel(request, pk);
		} else {
			return DataResponse.withStatus(HttpStatus.BAD_REQUEST).withError("error.onpatch", "No valid PK provided.");
		}
	}

	protected <T extends Item> DataResponse partiallyUpdateModel(final Request request, final long pk) {
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
			T oldItem = modelService.get(type, pk);

			if (oldItem == null) {
				throw new ModelNotFoundException(String.format("Item with PK=%s not found", pk));
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
					"No item with the given PK found to update.");
		} catch (final DeserializationException e) {
			return DataResponse.withStatus(HttpStatus.PRECONDITION_FAILED).withError("error.onpartialupdate",
					"Could not deserialize body json content.");
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
			item = serializationService.fromJson(request.body(), objectToUpdate);
		} else {
			item = serializationService.fromJson(request.body(), type);
		}

		if (item == null) {
			throw new DeserializationException("Request body was empty");
		}

		return item;
	}

	protected JsonNode deserializeToJsonToken(final Request request)
			throws UnknownTypeException, DeserializationException {
		final String content = request.body();

		return serializationService.fromJson(content, JsonNode.class);
	}

	private static DataResponse RESPONSE_UNKNOWN_TYPE = DataResponse.withStatus(HttpStatus.BAD_REQUEST)
			.withError("error.ongetall", "Unknown item type.");
}

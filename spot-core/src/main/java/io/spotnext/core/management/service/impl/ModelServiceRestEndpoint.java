package io.spotnext.core.management.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.exception.DeserializationException;
import io.spotnext.core.infrastructure.exception.ModelNotFoundException;
import io.spotnext.core.infrastructure.exception.ModelSaveException;
import io.spotnext.core.infrastructure.exception.ModelValidationException;
import io.spotnext.core.infrastructure.exception.UnknownTypeException;
import io.spotnext.core.infrastructure.http.HttpResponse;
import io.spotnext.core.infrastructure.http.HttpStatus;
import io.spotnext.core.infrastructure.http.Payload;
import io.spotnext.core.infrastructure.http.Status;
import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.infrastructure.support.MimeType;
import io.spotnext.core.management.annotation.Handler;
import io.spotnext.core.management.annotation.RemoteEndpoint;
import io.spotnext.core.management.support.data.PageableData;
import io.spotnext.core.management.transformer.JsonResponseTransformer;
import io.spotnext.core.persistence.exception.ModelNotUniqueException;
import io.spotnext.core.persistence.exception.QueryException;
import io.spotnext.core.persistence.query.JpqlQuery;
import io.spotnext.core.persistence.query.ModelQuery;
import io.spotnext.core.persistence.query.QueryResult;
import io.spotnext.core.persistence.service.QueryService;
import io.spotnext.core.support.util.MiscUtil;
import io.spotnext.core.types.Item;
import spark.Request;
import spark.Response;
import spark.route.HttpMethod;

@RemoteEndpoint(portConfigKey = "service.typesystem.rest.port", port = 19000, pathMapping = "/v1/models")
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
	 * @param request
	 * @param response
	 * @return the fetched item instance
	 * @throws UnknownTypeException
	 */
	@Handler(method = HttpMethod.get, pathMapping = "/:typecode", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse<PageableData<T>> getModels(final Request request, final Response response)
			throws UnknownTypeException {

		final HttpResponse<PageableData<T>> body = new HttpResponse<>();

		List<T> models = null;

		final int page = MiscUtil.intOrDefault(request.queryParams("page"), DEFAULT_PAGE);
		final int pageSize = MiscUtil.intOrDefault(request.queryParams("pageSize"), DEFAULT_PAGE_SIZE);

		try {
			final Class<? extends Item> type = typeService.getClassForTypeCode(request.params(":typecode"));
			final ModelQuery<? extends Item> query = new ModelQuery<>(type, null);
			query.setPage(page);
			query.setPageSize(pageSize);
			query.setEagerFetchRelations(true);
			models = (List<T>) modelService.getAll(query);

			body.setBody(Payload.of(new PageableData<>(models, page, pageSize)));
		} catch (final UnknownTypeException e) {
			body.setStatusCode(HttpStatus.BAD_REQUEST);
			body.getBody().addError(new Status("error.ongetall", "Unknown item type."));
		}

		return body;
	}

	/**
	 * Gets an item based on the PK.
	 * 
	 * @param request
	 * @param response
	 * @throws ModelNotFoundException
	 * @throws UnknownTypeException
	 */
	@Handler(method = HttpMethod.get, pathMapping = "/:typecode/:pk", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse<T> getModel(final Request request, final Response response)
			throws ModelNotFoundException, UnknownTypeException {

		final HttpResponse<T> body = new HttpResponse<>();

		final String typeCode = request.params(":typecode");
		final long pk = MiscUtil.longOrDefault(request.params(":pk"), -1);

		if (pk > 0) {
			final Class<T> type = (Class<T>) typeService.getClassForTypeCode(typeCode);
			final T model = modelService.get(type, pk);

			if (model == null) {
				body.setStatusCode(HttpStatus.NOT_FOUND);
			}

			body.setBody(Payload.of(model));
		} else {
			body.setStatusCode(HttpStatus.BAD_REQUEST);
			body.getBody().addError(new Status("error.onget", "No valid PK provided."));
		}

		return body;
	}

	/**
	 * Gets an item based on the search query. The query is a JPQL WHERE
	 * clause.<br />
	 * Example: .../country/query?q=isoCode = 'CZ' AND isoCode NOT LIKE 'A%'
	 * <br/>
	 * 
	 * @throws UnknownTypeException
	 */
	@Handler(method = HttpMethod.get, pathMapping = "/:typecode/query/", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> Object queryModel(final Request request, final Response response)
			throws UnknownTypeException {

		final HttpResponse<QueryResult<T>> body = new HttpResponse<>();

		final String typeCode = request.params(":typecode");
		final Class<T> type = (Class<T>) typeService.getClassForTypeCode(typeCode);
		final String[] queryParamValues = request.queryParamsValues("q");

		if (ArrayUtils.isNotEmpty(queryParamValues)) {
			final JpqlQuery<T> query = new JpqlQuery<>(
					String.format("SELECT x FROM %s x WHERE %s", type.getSimpleName(), queryParamValues[0]), type);
			query.setEagerFetchRelations(true);

			try {
				final QueryResult<T> result = queryService.query(query);
				body.setBody(Payload.of(result));
			} catch (final QueryException e) {
				body.setStatusCode(HttpStatus.BAD_REQUEST);
				body.getBody()
						.addError(new Status("error.query.execution", "Cannot execute given query: " + e.getMessage()));
			}
		} else {
			body.setStatusCode(HttpStatus.PRECONDITION_FAILED);
			body.getBody().addError(new Status("query.error", "Query could not be parsed."));
		}

		return body;
	}

	/**
	 * Gets item based on an example. <br/>
	 * <br/>
	 * {@link ModelService#get(Class, Map)} is called (=search by example).
	 * 
	 * @throws UnknownTypeException
	 */
	@SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
	@Handler(method = HttpMethod.post, pathMapping = "/:typecode/query/", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> Object queryModelByExample(final Request request, final Response response)
			throws UnknownTypeException {

		final HttpResponse<List<T>> body = new HttpResponse<>();

		T example = null;

		try {
			example = deserializeToItem(request);
			modelService.getByExample(example);
		} catch (UnknownTypeException | DeserializationException e) {
			body.getBody().addError(
					new Status("query.unknowntype", "Could not deserialize request body into valid example item."));
		}

		if (example != null) {
			final List<T> items = modelService.getAllByExample(example);
			body.setBody(Payload.of(items));
		}

		return body;
	}

	/**
	 * Creates a new item. If the item is not unique (based on its unique
	 * properties), an error is returned.
	 * 
	 * @param request
	 * @param response
	 * @throws UnknownTypeException
	 * @throws ModelSaveException
	 */
	@SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
	@Handler(method = HttpMethod.post, pathMapping = "/:typecode", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse<Map<String, Object>> createModel(final Request request,
			final Response response) throws UnknownTypeException, ModelSaveException {

		final Payload<Map<String, Object>> payload = new Payload<>();

		final HttpResponse<Map<String, Object>> body = new HttpResponse<>(payload, HttpStatus.PRECONDITION_FAILED);

		try {
			final T item = deserializeToItem(request);

			modelService.save(item);

			payload.setData(Collections.singletonMap("pk", item.getPk()));
			body.setStatusCode(HttpStatus.CREATED);
		} catch (final DeserializationException e) {
			body.getBody().addError(new Status("error.oncreate", e.getMessage()));
		} catch (final ModelNotUniqueException e) {
			body.setStatusCode(HttpStatus.CONFLICT);
			body.getBody().addError(new Status("error.model.notunique",
					"Another item with the same uniqueness criteria (but a different PK) was found."));
		} catch (final ModelValidationException e) {
			final List<String> messages = e.getConstraintViolations().stream().map((c) -> {
				return String.format("%s.%s could not be set to {%s}: %s", c.getRootBean().getClass().getSimpleName(),
						c.getPropertyPath(), c.getInvalidValue(), c.getMessage());
			}).collect(Collectors.toList());

			body.setStatusCode(HttpStatus.CONFLICT);
			body.getBody().addError(new Status("error.model.validation", String.join("\n", messages)));
		}

		return body;
	}

	/**
	 * Removes the given item. The PK or a search criteria has to be set.
	 * 
	 * @param request
	 * @param response
	 * @throws UnknownTypeException
	 * @throws ModelSaveException
	 */
	@Handler(method = HttpMethod.delete, pathMapping = "/:typecode/:pk", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse<Void> deleteModel(final Request request, final Response response)
			throws UnknownTypeException, ModelSaveException {

		final HttpResponse<Void> body = new HttpResponse<>();

		final String typeCode = request.params(":typecode");
		final long pk = MiscUtil.longOrDefault(request.params(":pk"), -1);

		if (pk > -1) {
			final Class<T> type = (Class<T>) typeService.getClassForTypeCode(typeCode);
			try {
				modelService.remove(type, pk);
			} catch (final ModelNotFoundException e) {
				body.setStatusCode(HttpStatus.NOT_FOUND);
				body.getBody().addError(new Status("error.ondelete", "Item with given PK not found."));
			}
		} else {
			body.setStatusCode(HttpStatus.PRECONDITION_FAILED);
			body.getBody().addError(new Status("error.ondelete", "No valid PK given."));
		}

		body.setStatusCode(HttpStatus.ACCEPTED);

		return body;
	}

	/**
	 * Updates an existing or creates the item with the given values. The PK
	 * must be provided. If the new item is not unique, an error is
	 * returned.<br/>
	 * Attention: fields that are omitted will be treated as @null. If you just
	 * want to update a few fields, use the PATCH Method.
	 * 
	 * @param request
	 * @param response
	 * @throws UnknownTypeException
	 * @throws ModelSaveException
	 */
	@Handler(method = HttpMethod.put, pathMapping = "/:typecode/:pk", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse<Void> createOrUpdateModel(final Request request, final Response response)
			throws UnknownTypeException, ModelSaveException {

		return partiallyUpdateModel(request, response);
	}

	@Handler(method = HttpMethod.patch, pathMapping = "/:typecode/:pk", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse<Void> partiallyUpdateModel(final Request request, final Response response)
			throws UnknownTypeException, ModelSaveException {

		final HttpResponse<Void> body = new HttpResponse<>();

		// get type
		final String typeCode = request.params(":typecode");
		final Class<T> type = (Class<T>) typeService.getClassForTypeCode(typeCode);

		final long pk = MiscUtil.longOrDefault(request.params(":pk"), -1);

		if (pk > 0) {
			try {

				// search old item
				T oldItem = modelService.get(type, pk);

				if (oldItem == null) {
					throw new ModelNotFoundException(String.format("Item with PK=%s not  found", pk));
				}

				// get body as json object
				oldItem = deserializeToItem(request, oldItem);
				oldItem.markAsDirty();

				modelService.save(oldItem);

				body.setStatusCode(HttpStatus.ACCEPTED);
			} catch (final ModelNotUniqueException | ModelValidationException e) {
				body.setStatusCode(HttpStatus.CONFLICT);
				body.getBody().addError(new Status("error.onpartialupdate", e.getMessage()));
			} catch (final ModelNotFoundException e) {
				body.setStatusCode(HttpStatus.NOT_FOUND);
				body.getBody()
						.addError(new Status("error.onpartialupdate", "No item with the given PK found to update."));
			} catch (final DeserializationException e) {
				body.setStatusCode(HttpStatus.PRECONDITION_FAILED);
				body.getBody()
						.addError(new Status("error.onpartialupdate", "Could not deserialize body json content."));
			}
		} else {
			body.setStatusCode(HttpStatus.BAD_REQUEST);
			body.getBody().addError(new Status("error.onpatch", "No valid PK provided."));
		}

		return body;
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

}
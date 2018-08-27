package io.spotnext.core.management.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
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
import io.spotnext.core.support.util.MiscUtil;
import io.spotnext.core.types.Item;
import spark.Request;
import spark.Response;
import spark.route.HttpMethod;

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
	 * @param request
	 * @param response
	 * @return the fetched item instance
	 * @throws UnknownTypeException
	 */
	@Handler(method = HttpMethod.get, pathMapping = "/:typecode", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse getModels(final Request request, final Response response)
			throws UnknownTypeException {

		final int page = MiscUtil.intOrDefault(request.queryParams("page"), DEFAULT_PAGE);
		final int pageSize = MiscUtil.intOrDefault(request.queryParams("pageSize"), DEFAULT_PAGE_SIZE);

		try {
			final Class<T> type = (Class<T>) typeService.getClassForTypeCode(request.params(":typecode"));
			final ModelQuery<T> query = new ModelQuery<>(type, null);
			query.setPage(page);
			query.setPageSize(pageSize);
			query.setEagerFetchRelations(true);
			final List<T> models = (List<T>) modelService.getAll(query);

			final PageablePayload<T> pageableData = new PageablePayload<>(models, page, pageSize);

			return DataResponse.ok().withPayload(pageableData);
		} catch (final UnknownTypeException e) {
			return DataResponse.withStatus(HttpStatus.BAD_REQUEST).withError("error.ongetall", "Unknown item type.");
		}
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
	public <T extends Item> DataResponse getModel(final Request request, final Response response)
			throws ModelNotFoundException, UnknownTypeException {

		final String typeCode = request.params(":typecode");
		final long pk = MiscUtil.longOrDefault(request.params(":pk"), -1);

		if (pk > 0) {
			final Class<T> type = (Class<T>) typeService.getClassForTypeCode(typeCode);
			final T model = modelService.get(type, pk);

			if (model == null) {
				return DataResponse.notFound();
			}

			return DataResponse.ok().withPayload(model);
		} else {
			return DataResponse.withStatus(HttpStatus.BAD_REQUEST).withError("error.onget", "No valid PK provided.");
		}
	}

	/**
	 * Gets an item based on the search query. The query is a JPQL WHERE
	 * clause.<br />
	 * Example: .../country/query?q=isoCode = 'CZ' AND isoCode NOT LIKE 'A%' <br/>
	 * 
	 * @throws UnknownTypeException
	 */
	@Handler(method = HttpMethod.get, pathMapping = "/:typecode/query/", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> DataResponse queryModel(final Request request, final Response response)
			throws UnknownTypeException {

		final String typeCode = request.params(":typecode");
		final Class<T> type = (Class<T>) typeService.getClassForTypeCode(typeCode);
		final String[] queryParamValues = request.queryParamsValues("q");

		if (ArrayUtils.isNotEmpty(queryParamValues)) {
			final JpqlQuery<T> query = new JpqlQuery<>(
					String.format("SELECT x FROM %s x WHERE %s", type.getSimpleName(), queryParamValues[0]), type);
			query.setEagerFetchRelations(true);

			try {
				final QueryResult<T> result = queryService.query(query);
				return DataResponse.ok().withPayload(result);
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

		try {
			final T example = deserializeToItem(request);
			final List<T> items = modelService.getAllByExample(example);

			return DataResponse.ok().withPayload(items);
		} catch (UnknownTypeException | DeserializationException e) {
			return DataResponse.withStatus(HttpStatus.BAD_REQUEST).withError("query.unknowntype",
					"Could not deserialize request body into valid example item.");
		}
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
	public <T extends Item> DataResponse createModel(final Request request, final Response response)
			throws UnknownTypeException, ModelSaveException {

		try {
			final T item = deserializeToItem(request);
			modelService.save(item);

			return DataResponse.created().withPayload(Collections.singletonMap("pk", item.getPk()));
		} catch (final DeserializationException e) {
			return DataResponse.withStatus(HttpStatus.BAD_REQUEST).withError("error.oncreate", e.getMessage());
		} catch (final ModelNotUniqueException e) {
			return DataResponse.withStatus(HttpStatus.CONFLICT).withError("error.model.notunique",
					"Another item with the same uniqueness criteria (but a different PK) was found.");
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
	 * @param request
	 * @param response
	 * @throws UnknownTypeException
	 * @throws ModelSaveException
	 */
	@Handler(method = HttpMethod.delete, pathMapping = "/:typecode/:pk", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> DataResponse deleteModel(final Request request, final Response response)
			throws UnknownTypeException, ModelSaveException {

		final String typeCode = request.params(":typecode");
		final long pk = MiscUtil.longOrDefault(request.params(":pk"), -1);

		if (pk > -1) {
			final Class<T> type = (Class<T>) typeService.getClassForTypeCode(typeCode);
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
	 * Updates an existing or creates the item with the given values. The PK must be
	 * provided. If the new item is not unique, an error is returned.<br/>
	 * Attention: fields that are omitted will be treated as @null. If you just want
	 * to update a few fields, use the PATCH Method.
	 * 
	 * @param request
	 * @param response
	 * @throws UnknownTypeException
	 * @throws ModelSaveException
	 */
	@Handler(method = HttpMethod.put, pathMapping = "/:typecode/:pk", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> DataResponse createOrUpdateModel(final Request request, final Response response)
			throws UnknownTypeException, ModelSaveException {

		return partiallyUpdateModel(request, response);
	}

	@Handler(method = HttpMethod.patch, pathMapping = "/:typecode/:pk", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> DataResponse partiallyUpdateModel(final Request request, final Response response)
			throws UnknownTypeException, ModelSaveException {

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

				return DataResponse.accepted();
			} catch (final ModelNotUniqueException | ModelValidationException e) {
				return DataResponse.conflict().withError("error.onpartialupdate", e.getMessage());
			} catch (final ModelNotFoundException e) {
				return DataResponse.notFound().withError("error.onpartialupdate",
						"No item with the given PK found to update.");
			} catch (final DeserializationException e) {
				return DataResponse.withStatus(HttpStatus.PRECONDITION_FAILED).withError("error.onpartialupdate",
						"Could not deserialize body json content.");
			}
		} else {
			return DataResponse.withStatus(HttpStatus.BAD_REQUEST).withError("error.onpatch", "No valid PK provided.");
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

}
package at.spot.core.management.service.impl;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import at.spot.core.persistence.query.QueryCondition;
import at.spot.core.persistence.query.QueryResult;

import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.exception.ModelValidationException;
import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.service.ConfigurationService;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.type.ItemTypePropertyDefinition;
import at.spot.core.infrastructure.type.MimeType;
import at.spot.core.management.annotation.Handler;
import at.spot.core.management.data.PageableData;
import at.spot.core.management.exception.RemoteServiceInitException;
import at.spot.core.management.transformer.JsonResponseTransformer;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.exception.QueryException;
import at.spot.core.persistence.service.QueryService;
import at.spot.core.support.util.MiscUtil;
import spark.Request;
import spark.Response;
import spark.route.HttpMethod;

@Service
public class ModelServiceRestEndpoint extends AbstractHttpServiceEndpoint {

	private static final String CONFIG_KEY_PORT = "service.model.rest.port";
	private static final int DEFAULT_PORT = 19001;

	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_PAGE_SIZE = 100;

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected ConfigurationService configurationService;

	@Autowired
	protected ModelService modelService;

	@Autowired
	protected QueryService queryService;

	protected final Gson gson = new Gson();

	@PostConstruct
	@Override
	public void init() throws RemoteServiceInitException {
		loggingService.info(String.format("Initiating remote model REST service on port %s", getPort()));
		super.init();
	}

	/**
	 * Gets all items of the given item type. The page index starts at 1.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws UnknownTypeException
	 */
	@Handler(method = HttpMethod.get, pathMapping = "/v1/models/:typecode", mimeType = MimeType.JAVASCRIPT, responseTransformer = JsonResponseTransformer.class)
	public Object getModels(final Request request, final Response response) throws UnknownTypeException {
		final RequestStatus status = RequestStatus.success();

		List<? extends Item> models = new ArrayList<>();

		final int page = MiscUtil.intOrDefault(request.queryParams("page"), DEFAULT_PAGE);
		final int pageSize = MiscUtil.intOrDefault(request.queryParams("pageSize"), DEFAULT_PAGE_SIZE);
		final Class<? extends Item> type = typeService.getType(request.params(":typecode"));

		models = modelService.getAll(type, null, page - 1, pageSize, false);

		return returnDataAndStatus(response, status.payload(new PageableData(models, page, pageSize)));
	}

	/**
	 * Gets an item based on the PK.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ModelNotFoundException
	 * @throws UnknownTypeException
	 */
	@Handler(method = HttpMethod.get, pathMapping = "/v1/models/:typecode/:pk", mimeType = MimeType.JAVASCRIPT, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> Object getModel(final Request request, final Response response)
			throws ModelNotFoundException, UnknownTypeException {

		final RequestStatus status = RequestStatus.success();

		final String typeCode = request.params(":typecode");
		final long pk = MiscUtil.longOrDefault(request.params(":pk"), -1);

		final Class<T> type = (Class<T>) typeService.getType(typeCode);
		final T model = modelService.get(type, pk);

		if (model == null) {
			status.httpStatus(HttpStatus.NOT_FOUND_404);
		}

		return returnDataAndStatus(response, status.payload(model));
	}

	/**
	 * Gets an item based on the search query. The query is a JEXL expression.
	 * <br/>
	 * 
	 * <br/>
	 * Example: .../User/query/uid='test-user' & name.contains('Vader') <br/>
	 * <br/>
	 * {@link QueryService#query(Class, QueryCondition, Comparator, int, int)}
	 * is called.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws UnknownTypeException
	 */
	public <T extends Item> Object queryModelByQuery(final Request request, final Response response)
			throws UnknownTypeException {

		final RequestStatus status = RequestStatus.success();

		final int page = MiscUtil.intOrDefault(request.queryParams("page"), DEFAULT_PAGE);
		final int pageSize = MiscUtil.intOrDefault(request.queryParams("pageSize"), DEFAULT_PAGE_SIZE);
		final Class<? extends Item> type = typeService.getType(request.params(":typecode"));

		final String[] queryStrings = request.queryParamsValues("query");

		if (queryStrings != null && queryStrings.length > 0) {
			final String queryString = MiscUtil.removeEnclosingQuotes(queryStrings[0]);

			try {
				final QueryResult<T> result = (QueryResult<T>) queryService.query(type, queryString, null, page,
						pageSize);

				status.payload(result);
			} catch (final QueryException e) {
				status.httpStatus(HttpStatus.BAD_REQUEST_400).error("Cannot execute given query: " + e.getMessage());
			}
		} else {
			status.httpStatus(HttpStatus.PRECONDITION_FAILED_412).error("Query could not be parsed.");
		}

		return status;
	}

	/**
	 * Gets an item based on the search query. <br/>
	 * <br/>
	 * Example: .../User/query/?uid=test-user&name=LordVader. <br/>
	 * <br/>
	 * {@link ModelService#get(Class, Map)} is called (=search by example).
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws UnknownTypeException
	 */
	public <T extends Item> Object queryModelByExample(final Request request, final Response response)
			throws UnknownTypeException {
		final RequestStatus status = RequestStatus.success();

		final int page = MiscUtil.intOrDefault(request.queryParams("page"), DEFAULT_PAGE);
		final int pageSize = MiscUtil.intOrDefault(request.queryParams("pageSize"), DEFAULT_PAGE_SIZE);

		final String typeCode = request.params(":typecode");
		final Class<T> type = (Class<T>) typeService.getType(typeCode);

		final Map<String, String[]> query = request.queryMap().toMap();
		final Map<String, Comparable<?>> searchParameters = new HashMap<>();

		for (final ItemTypePropertyDefinition prop : typeService.getItemTypeProperties(typeCode).values()) {
			final String[] queryValues = query.get(prop.name);

			if (queryValues != null && queryValues.length == 1) {
				final Class<?> propertyType = prop.returnType;

				final Object value = serializationService.fromJson(queryValues[0], propertyType);

				if (value instanceof Comparable | value == null) {
					searchParameters.put(prop.name, (Comparable<?>) value);
				} else {
					status.warn(String.format("Unknown attribute value %s=%S in query", prop.name, value.toString()));
				}
			} else {
				status.warn(
						String.format("Query attribute %s passed more than once - only taking the first.", prop.name));
			}
		}

		final List<T> models = modelService.getAll(type, searchParameters, page, pageSize, false);

		if (models == null) {
			status.httpStatus(HttpStatus.NOT_FOUND_404);
		} else {
			status.payload(models);
		}

		return returnDataAndStatus(response, status);
	}

	@Handler(method = HttpMethod.get, pathMapping = "/v1/models/:typecode/query/", mimeType = MimeType.JAVASCRIPT, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> Object queryModel(final Request request, final Response response)
			throws UnknownTypeException {

		final String[] queryParamValues = request.queryParamsValues("query");

		if (queryParamValues != null && queryParamValues.length > 0) {
			return queryModelByQuery(request, response);
		} else {
			return queryModelByExample(request, response);
		}
	}

	/**
	 * Creates a new item. If the item is not unique (based on its unique
	 * properties), an error is returned.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws UnknownTypeException
	 * @throws ModelSaveException
	 */
	@Handler(method = HttpMethod.put, pathMapping = "/v1/models/:typecode", mimeType = MimeType.JAVASCRIPT, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> RequestStatus createModel(final Request request, final Response response)
			throws UnknownTypeException, ModelSaveException {

		final RequestStatus status = RequestStatus.success();

		final T item = deserializeToItem(request);

		if (item.pk != null) {
			status.warn("PK was reset, itmay not be set for new items.");
			item.pk = null;
		}

		try {
			modelService.save(item);
			response.status(HttpStatus.CREATED_201);
		} catch (final ModelNotUniqueException | ModelValidationException e) {
			status.httpStatus(HttpStatus.CONFLICT_409)
					.error("Another item with the same uniqueness criteria (but a different PK was found.");
		}

		return returnDataAndStatus(response, status);
	}

	/**
	 * Removes the given item. The PK or a search criteria has to be set.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws UnknownTypeException
	 * @throws ModelSaveException
	 */
	@Handler(method = HttpMethod.delete, pathMapping = "/v1/models/:typecode", mimeType = MimeType.JAVASCRIPT, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> RequestStatus deleteModel(final Request request, final Response response)
			throws UnknownTypeException, ModelSaveException {

		final RequestStatus status = RequestStatus.success();

		final String typeCode = request.params(":typecode");
		final long pk = MiscUtil.longOrDefault(request.params(":pk"), -1);

		if (pk > -1) {
			final Class<T> type = (Class<T>) typeService.getType(typeCode);
			try {
				modelService.remove(type, pk);
			} catch (final ModelNotFoundException e) {
				status.httpStatus(HttpStatus.NOT_FOUND_404).error("Item with given PK not found.");
			}
		} else {
			status.httpStatus(HttpStatus.PRECONDITION_FAILED_412).error("No valid PK given.");
		}

		return returnDataAndStatus(response, status);
	}

	/**
	 * Updates an item with the given values. The PK must be provided. If the
	 * new item is not unique, an error is returned.<br/>
	 * Attention: fields that are omitted will be treated as @null. If you just
	 * want to update a few fields, use the PATCH Method.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws UnknownTypeException
	 * @throws ModelSaveException
	 */
	@Handler(method = HttpMethod.post, pathMapping = "/v1/models/:typecode", mimeType = MimeType.JAVASCRIPT, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> RequestStatus updateModel(final Request request, final Response response)
			throws UnknownTypeException, ModelSaveException {

		final RequestStatus status = RequestStatus.success();

		final T item = deserializeToItem(request);

		if (item.pk == null) {
			status.httpStatus(HttpStatus.PRECONDITION_FAILED_412).error("You cannot update a new item (PK was null)");
		} else {
			try {
				modelService.save(item);
				response.status(HttpStatus.ACCEPTED_202);
				item.markAsDirty();
			} catch (final ModelNotUniqueException | ModelValidationException e) {
				status.httpStatus(HttpStatus.CONFLICT_409)
						.error("Another item with the same uniqueness criteria (but a different PK was found.");
			}
		}

		return returnDataAndStatus(response, status);
	}

	@Handler(method = HttpMethod.patch, pathMapping = "/v1/models/:typecode/:pk", mimeType = MimeType.JAVASCRIPT, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> RequestStatus partiallyUpdateModel(final Request request, final Response response)
			throws UnknownTypeException, ModelSaveException {

		final RequestStatus status = RequestStatus.success();

		// get type
		final String typeCode = request.params(":typecode");
		final Class<T> type = (Class<T>) typeService.getType(typeCode);

		final long pk = MiscUtil.longOrDefault(request.params(":pk"), -1);

		// get body as json object
		final JsonObject content = deserializeToJsonToken(request);

		if (content != null && pk >= 0) {
			try {
				// search old item
				final T oldItem = modelService.get(type, pk);

				final Map<String, ItemTypePropertyDefinition> propertyDefinitions = typeService
						.getItemTypeProperties(type);

				for (final Entry<String, JsonElement> prop : content.entrySet()) {
					final String key = prop.getKey();
					final JsonElement value = prop.getValue();

					final ItemTypePropertyDefinition propDef = propertyDefinitions.get(key);

					// if the json property really exists on the item, then
					// continue
					if (propDef != null) {
						final Object parsedValue = gson.fromJson(value, propDef.returnType);
						oldItem.setProperty(prop.getKey(), parsedValue);
					}
				}

				oldItem.markAsDirty();

				modelService.save(oldItem);

				response.status(HttpStatus.ACCEPTED_202);
			} catch (final ModelNotUniqueException | ModelValidationException e) {
				status.httpStatus(HttpStatus.CONFLICT_409)
						.error("Another item with the same uniqueness criteria (but a different PK was found.");
			} catch (final ModelNotFoundException e) {
				status.httpStatus(HttpStatus.NOT_FOUND_404).error("No item with the given PK found to update.");
			}
		} else {
			status.httpStatus(HttpStatus.PRECONDITION_FAILED_412).error("Could not deserialize body json content");
		}

		return returnDataAndStatus(response, status);
	}

	protected RequestStatus returnDataAndStatus(final Response response, final RequestStatus status,
			final Object payload) {
		status.payload(payload);
		return returnDataAndStatus(response, status);
	}

	protected RequestStatus returnDataAndStatus(final Response response, final RequestStatus status) {
		response.status(status.httpStatus());
		return status;
	}

	protected <T extends Item> T deserializeToItem(final Request request) throws UnknownTypeException {
		final String typeCode = request.params(":typecode");
		final Class<T> type = (Class<T>) typeService.getType(typeCode);

		return serializationService.fromJson(request.body(), type);
	}

	protected JsonObject deserializeToJsonToken(final Request request) throws UnknownTypeException {
		final String content = request.body();

		return serializationService.fromJson(content);
	}

	@Override
	public int getPort() {
		return configurationService.getInteger(CONFIG_KEY_PORT, DEFAULT_PORT);
	}

	@Override
	public InetAddress getBindAddress() {
		// not used
		// we listen everywhere
		return null;
	}
}

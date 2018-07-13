package at.spot.core.management.service.impl;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import at.spot.core.persistence.query.JpqlQuery;
import at.spot.core.persistence.query.ModelQuery;
import at.spot.core.persistence.query.QueryResult;

import at.spot.core.infrastructure.exception.DeserializationException;
import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.exception.ModelValidationException;
import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.http.HttpResponse;
import at.spot.core.infrastructure.http.HttpStatus;
import at.spot.core.infrastructure.http.Payload;
import at.spot.core.infrastructure.http.Status;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.support.ItemTypeDefinition;
import at.spot.core.infrastructure.support.ItemTypePropertyDefinition;
import at.spot.core.infrastructure.support.MimeType;
import at.spot.core.management.annotation.Handler;
import at.spot.core.management.converter.Converter;
import at.spot.core.management.exception.RemoteServiceInitException;
import at.spot.core.management.support.data.GenericItemDefinitionData;
import at.spot.core.management.support.data.PageableData;
import at.spot.core.management.transformer.JsonResponseTransformer;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.exception.QueryException;
import at.spot.core.persistence.service.QueryService;
import at.spot.core.support.util.MiscUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import spark.Request;
import spark.Response;
import spark.route.HttpMethod;

@Service
public class TypeSystemServiceRestEndpoint extends AbstractHttpServiceEndpoint {

	private static final String CONFIG_KEY_PORT = "service.typesystem.rest.port";
	private static final int DEFAULT_PORT = 19000;

	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_PAGE_SIZE = 100;

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected ModelService modelService;

	@Autowired
	protected QueryService queryService;

	@Autowired
	protected Converter<ItemTypeDefinition, GenericItemDefinitionData> itemTypeConverter;

	@PostConstruct
	@Override
	public void init() throws RemoteServiceInitException {
		loggingService.info(String.format("Initiating remote type system REST service on port %s", getPort()));
		super.init();
	}

	/*
	 * TYPES
	 */

	@Handler(pathMapping = "/types/", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public List<GenericItemDefinitionData> getTypes(final Request request, final Response response)
			throws UnknownTypeException {

		final List<GenericItemDefinitionData> types = new ArrayList<>();

		for (final String typeCode : typeService.getItemTypeDefinitions().keySet()) {
			final ItemTypeDefinition def = typeService.getItemTypeDefinition(typeCode);

			final GenericItemDefinitionData d = itemTypeConverter.convert(def);
			types.add(d);
		}

		return types;
	}

	@Handler(pathMapping = "/types/:typecode", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public GenericItemDefinitionData getType(final Request request, final Response response)
			throws UnknownTypeException {
		GenericItemDefinitionData ret = null;

		final String typeCode = request.params(":typecode");

		if (StringUtils.isNotBlank(typeCode)) {
			final ItemTypeDefinition def = typeService.getItemTypeDefinitions().get(StringUtils.lowerCase(typeCode));

			if (def != null) {
				ret = itemTypeConverter.convert(def);
			} else {
				throw new UnknownTypeException(String.format("Type %s unknown.", typeCode));
			}
		}

		return ret;
	}

	/*
	 * MODELS
	 */

	/**
	 * Gets all items of the given item type. The page index starts at 1.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws UnknownTypeException
	 */
	@Handler(method = HttpMethod.get, pathMapping = "/v1/models/:typecode", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
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
		} catch (UnknownTypeException e) {
			body.setStatusCode(HttpStatus.NOT_FOUND);
		}

		return body;
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
	@Handler(method = HttpMethod.get, pathMapping = "/v1/models/:typecode/:pk", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse<T> getModel(final Request request, final Response response)
			throws ModelNotFoundException, UnknownTypeException {

		final HttpResponse<T> body = new HttpResponse<>();

		final String typeCode = request.params(":typecode");
		final long pk = MiscUtil.longOrDefault(request.params(":pk"), -1);

		final Class<T> type = (Class<T>) typeService.getClassForTypeCode(typeCode);
		final T model = modelService.get(type, pk);

		if (model == null) {
			body.setStatusCode(HttpStatus.NOT_FOUND);
		}

		body.setBody(Payload.of(model));

		return body;
	}

	/**
	 * Gets an item based on the search query. The query is a JPQL WHERE
	 * clause.<br />
	 * Example: .../country/query?q=isoCode = 'CZ' AND isoCode NOT LIKE 'A%' <br/>
	 * 
	 * @throws UnknownTypeException
	 */
	@Handler(method = HttpMethod.get, pathMapping = "/v1/models/:typecode/query/", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> Object queryModel(final Request request, final Response response)
			throws UnknownTypeException {

		final HttpResponse<QueryResult<T>> body = new HttpResponse<>();

		final String typeCode = request.params(":typecode");
		final Class<T> type = (Class<T>) typeService.getClassForTypeCode(typeCode);
		final String[] queryParamValues = request.queryParamsValues("q");

		if (ArrayUtils.isNotEmpty(queryParamValues)) {
			JpqlQuery<T> query = new JpqlQuery<>(
					String.format("SELECT x FROM %s x WHERE %s", type.getSimpleName(), queryParamValues[0]), type);
			query.setEagerFetchRelations(true);

			try {
				QueryResult<T> result = queryService.query(query);
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
	@Handler(method = HttpMethod.post, pathMapping = "/v1/models/:typecode/query/", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> Object queryModelByExample(final Request request, final Response response)
			throws UnknownTypeException {

		final HttpResponse<List<T>> body = new HttpResponse<>();

		final int page = MiscUtil.intOrDefault(request.queryParams("page"), DEFAULT_PAGE);
		final int pageSize = MiscUtil.intOrDefault(request.queryParams("pageSize"), DEFAULT_PAGE_SIZE);

		final String typeCode = request.params(":typecode");
		final Class<T> type = (Class<T>) typeService.getClassForTypeCode(typeCode);

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
	 * @return
	 * @throws UnknownTypeException
	 * @throws ModelSaveException
	 */
	@SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
	@Handler(method = HttpMethod.post, pathMapping = "/v1/models/:typecode", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse<Map<String, Object>> createModel(final Request request,
			final Response response) throws UnknownTypeException, ModelSaveException {

		Payload<Map<String, Object>> payload = new Payload<>();

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
	 * @return
	 * @throws UnknownTypeException
	 * @throws ModelSaveException
	 */
	@Handler(method = HttpMethod.delete, pathMapping = "/v1/models/:typecode/:pk", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
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
	 * Updates an existing or creates the item with the given values. The PK must be
	 * provided. If the new item is not unique, an error is returned.<br/>
	 * Attention: fields that are omitted will be treated as @null. If you just want
	 * to update a few fields, use the PATCH Method.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws UnknownTypeException
	 * @throws ModelSaveException
	 */
	@Handler(method = HttpMethod.put, pathMapping = "/v1/models/:typecode/:pk", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse<Void> createOrUpdateModel(final Request request, final Response response)
			throws UnknownTypeException, ModelSaveException {

		return partiallyUpdateModel(request, response);
	}

	@Handler(method = HttpMethod.patch, pathMapping = "/v1/models/:typecode/:pk", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpResponse<Void> partiallyUpdateModel(final Request request, final Response response)
			throws UnknownTypeException, ModelSaveException {

		final HttpResponse<Void> body = new HttpResponse<>();

		// get type
		final String typeCode = request.params(":typecode");
		final Class<T> type = (Class<T>) typeService.getClassForTypeCode(typeCode);

		final long pk = MiscUtil.longOrDefault(request.params(":pk"), -1);

		try {
			// get body as json object
			final JsonObject content = deserializeToJsonToken(request);

			// search old item
			final T oldItem = modelService.get(type, pk);

			if (oldItem == null) {
				throw new ModelNotFoundException(String.format("Item with PK=%s not  found", pk));
			}

			final Map<String, ItemTypePropertyDefinition> propertyDefinitions = typeService
					.getItemTypeProperties(typeService.getTypeCodeForClass(type));

			for (final Entry<String, JsonElement> prop : content.entrySet()) {
				final String key = prop.getKey();
				final JsonElement value = prop.getValue();

				final ItemTypePropertyDefinition propDef = propertyDefinitions.get(key);

				// if the json property really exists on the item, then
				// continue
				if (propDef != null) {
					final Object parsedValue = serializationService.fromJson(value.toString(), propDef.getReturnType());
					modelService.setPropertyValue(oldItem, prop.getKey(), parsedValue);
				}
			}

			oldItem.markAsDirty();

			modelService.save(oldItem);

			body.setStatusCode(HttpStatus.ACCEPTED);
		} catch (final ModelNotUniqueException | ModelValidationException e) {
			body.setStatusCode(HttpStatus.CONFLICT);
			body.getBody().addError(new Status("error.onpartialupdate",
					"Another item with the same uniqueness criteria (but a different PK) was found."));
		} catch (final ModelNotFoundException e) {
			body.setStatusCode(HttpStatus.NOT_FOUND);
			body.getBody().addError(new Status("error.onpartialupdate", "No item with the given PK found to update."));
		} catch (final DeserializationException e) {
			body.setStatusCode(HttpStatus.PRECONDITION_FAILED);
			body.getBody().addError(new Status("error.onpartialupdate", "Could not deserialize body json content."));
		}

		return body;
	}

	protected <T extends Item> T deserializeToItem(final Request request)
			throws DeserializationException, UnknownTypeException {

		final String typeCode = request.params(":typecode");
		final Class<T> type = (Class<T>) typeService.getClassForTypeCode(typeCode);

		final T item = serializationService.fromJson(request.body(), type);

		if (item == null) {
			throw new DeserializationException("Request body was empty");
		}

		return item;
	}

	protected JsonObject deserializeToJsonToken(final Request request)
			throws UnknownTypeException, DeserializationException {
		final String content = request.body();

		return serializationService.fromJson(content, JsonElement.class).getAsJsonObject();
	}

	/*
	 * 
	 */

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

package at.spot.core.management.service.impl;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.service.ConfigurationService;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.type.ItemTypeDefinition;
import at.spot.core.infrastructure.type.ItemTypePropertyDefinition;
import at.spot.core.management.annotation.Get;
import at.spot.core.management.annotation.Post;
import at.spot.core.management.annotation.Put;
import at.spot.core.management.data.GenericItemDefinitionData;
import at.spot.core.management.data.PageableData;
import at.spot.core.management.exception.RemoteServiceInitException;
import at.spot.core.management.transformer.JsonResponseTransformer;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.service.QueryService;
import at.spot.core.support.util.MiscUtil;
import spark.Request;
import spark.Response;

@Service
public class ModelServiceRestEndpoint extends AbstractHttpServiceEndpoint {

	private static final String CONFIG_KEY_PORT = "service.model.rest.port";
	private static final int DEFAULT_PORT = 9000;

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected ConfigurationService configurationService;

	@Autowired
	protected Converter<ItemTypeDefinition, GenericItemDefinitionData> itemTypeConverter;

	@Autowired
	protected ModelService modelService;

	@Autowired
	protected QueryService queryService;

	@PostConstruct
	@Override
	public void init() throws RemoteServiceInitException {
		loggingService.info(String.format("Initiating remote model REST service on port %s", getPort()));
		super.init();
	}

	@Get(pathMapping = "/v1/models/:typecode", mimeType = "application/javascript", responseTransformer = JsonResponseTransformer.class)
	public Object getModels(final Request request, final Response response)
			throws ModelNotFoundException, UnknownTypeException {

		List<? extends Item> models = new ArrayList<>();

		final int page = MiscUtil.intOrDefault(request.queryParams("page"), 1);
		final int pageSize = MiscUtil.intOrDefault(request.queryParams("pageSize"), 50);
		final String typeCode = request.params(":typecode");

		final Class<? extends Item> type = typeService.getType(typeCode);

		models = modelService.getAll(type);
		models = models.stream().skip(pageSize * (page - 1)).limit(pageSize).collect(Collectors.toList());

		return new PageableData(models, page, pageSize);
	}

	@Get(pathMapping = "/v1/models/:typecode/:pk", mimeType = "application/javascript", responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> Object getModel(final Request request, final Response response)
			throws ModelNotFoundException, UnknownTypeException {

		final String typeCode = request.params(":typecode");
		final long pk = MiscUtil.longOrDefault(request.params(":pk"), -1);

		final Class<T> type = (Class<T>) typeService.getType(typeCode);
		final T model = modelService.get(type, pk);

		if (model == null) {
			response.status(HttpStatus.NOT_FOUND_404);
		}

		return model;
	}

	@Get(pathMapping = "/v1/models/:typecode/query/", mimeType = "application/javascript", responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> Object queryModel(final Request request, final Response response)
			throws ModelNotFoundException, UnknownTypeException {

		final String typeCode = request.params(":typecode");
		final Map<String, String[]> query = request.queryMap().toMap();
		final Class<T> type = (Class<T>) typeService.getType(typeCode);

		final Map<String, Comparable<?>> searchParameters = new HashMap<>();

		for (final ItemTypePropertyDefinition prop : typeService.getItemTypeProperties(typeCode).values()) {
			final String[] queryValues = query.get(prop.name);

			if (queryValues != null && queryValues.length == 1) {
				try {
					final Class<?> propertyType = Class.forName(prop.returnType);

					final Object value = serializationService.fromJson(queryValues[0], propertyType);

					if (value instanceof Comparable) {
						searchParameters.put(prop.name, (Comparable) value);
					}
				} catch (final ClassNotFoundException e) {
					throw new ModelNotFoundException(e);
				}
			}
		}

		final Item model = modelService.get(type, searchParameters);

		return model;
	}

	/**
	 * Called when a new item should be created.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws UnknownTypeException
	 * @throws ModelSaveException
	 * @throws ModelNotUniqueException
	 */
	@Put(pathMapping = "/v1/models/:typecode", mimeType = "application/javascript", responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpMethodStatus createModel(final Request request, final Response response)
			throws UnknownTypeException, ModelSaveException, ModelNotUniqueException {

		final HttpMethodStatus status = new HttpMethodStatus(true);

		final T item = deserializeItem(request);

		if (item.pk != null) {
			status.warn("PK was reset, itmay not be set for new items.");
			item.pk = null;
		}

		modelService.save(item);
		response.status(HttpStatus.CREATED_201);

		return status;
	}

	@Post(pathMapping = "/v1/models/:typecode", mimeType = "application/javascript", responseTransformer = JsonResponseTransformer.class)
	public <T extends Item> HttpMethodStatus updateModel(final Request request, final Response response)
			throws UnknownTypeException, ModelSaveException, ModelNotUniqueException {

		final HttpMethodStatus status = new HttpMethodStatus(true);

		final T item = deserializeItem(request);

		if (item.pk == null) {
			status.error("You cannot update a new item (PK was null)");
			status.success(false);
		} else {
			item.markAsDirty();
			modelService.save(item);
			response.status(HttpStatus.CREATED_201);
		}

		return status;
	}

	protected Map<String, Object> returnDataAndStatus(final int httpStatus, final HttpMethodStatus status,
			final Object data) {

		final Map<String, Object> ret = new HashMap<>();

		ret.put("status", status);
		ret.put("content", data);

		return ret;
	}

	protected <T extends Item> T deserializeItem(final Request request) throws UnknownTypeException {
		final String typeCode = request.params(":typecode");
		final Class<T> type = (Class<T>) typeService.getType(typeCode);

		return serializationService.fromJson(request.body(), type);
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

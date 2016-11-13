package at.spot.core.management.service.impl;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.service.ConfigurationService;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.type.ItemTypeDefinition;
import at.spot.core.infrastructure.type.LogLevel;
import at.spot.core.management.annotation.Get;
import at.spot.core.management.data.GenericItemDefinitionData;
import at.spot.core.management.data.PageableData;
import at.spot.core.management.exception.RemoteServiceInitException;
import at.spot.core.management.transformer.JsonResponseTransformer;
import at.spot.core.model.Item;
import at.spot.core.support.util.MiscUtil;
import spark.Request;
import spark.Response;

@Service
public class TypeSystemRestServiceEndpoint extends AbstractHttpServiceEndpoint {

	private static final String CONFIG_KEY_PORT = "service.typesystem.restservice.port";
	private static final int DEFAULT_PORT = 9000;

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected ConfigurationService configurationService;

	@Autowired
	protected Converter<ItemTypeDefinition, GenericItemDefinitionData> itemTypeConverter;

	@Autowired
	protected ModelService modelService;

	@Log(logLevel = LogLevel.INFO, message = "Initiating remote type system access service ...")
	@PostConstruct
	@Override
	public void init() throws RemoteServiceInitException {
		super.init();
	}

	@Get(pathMapping = "/types/", mimeType = "application/javascript", responseTransformer = JsonResponseTransformer.class)
	public Object getTypes(final Request request, final Response response) {
		final List<GenericItemDefinitionData> types = new ArrayList<>();

		for (final String typeCode : typeService.getItemTypeDefinitions().keySet()) {
			final ItemTypeDefinition def = typeService.getItemTypeDefinition(typeCode);

			final GenericItemDefinitionData d = itemTypeConverter.convert(def);
			types.add(d);
		}

		return types;
	}

	@Get(pathMapping = "/types/:typecode", mimeType = "application/json", responseTransformer = JsonResponseTransformer.class)
	public Object getType(final Request request, final Response response) {
		GenericItemDefinitionData ret = null;

		final String typeCode = request.params(":typecode");

		if (StringUtils.isNotBlank(typeCode)) {
			final ItemTypeDefinition def = typeService.getItemTypeDefinitions().get(typeCode);

			ret = itemTypeConverter.convert(def);
		}

		return ret;
	}

	@Get(pathMapping = "/models/:typecode", mimeType = "application/javascript", responseTransformer = JsonResponseTransformer.class)
	public Object getModels(final Request request, final Response response) throws ModelNotFoundException {
		List<? extends Item> models = new ArrayList<>();

		final int page = MiscUtil.intOrDefault(request.queryParams("page"), 1);
		final int pageSize = MiscUtil.intOrDefault(request.queryParams("pageSize"), 50);

		final Class<? extends Item> type = typeService.getType(request.params(":typecode"));

		models = modelService.getAll(type);
		models = models.stream().skip(pageSize * (page - 1)).limit(pageSize).collect(Collectors.toList());

		return new PageableData(models, page, pageSize);
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

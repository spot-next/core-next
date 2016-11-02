package at.spot.core.management.service.impl;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

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
import at.spot.core.management.exception.RemoteServiceInitException;
import at.spot.core.management.transformer.JsonResponseTransformer;
import at.spot.core.model.Item;
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
	public Object getTypes(Request request, Response response) {
		List<GenericItemDefinitionData> types = new ArrayList<>();

		for (String typeCode : typeService.getItemTypeDefinitions().keySet()) {
			ItemTypeDefinition def = typeService.getItemTypeDefinition(typeCode);

			GenericItemDefinitionData d = itemTypeConverter.convert(def);
			types.add(d);
		}

		return types;
	}

	@Get(pathMapping = "/types/:typecode", mimeType = "application/json", responseTransformer = JsonResponseTransformer.class)
	public Object getType(Request request, Response response) {
		GenericItemDefinitionData ret = null;

		String typeCode = request.params(":typecode");

		if (StringUtils.isNotBlank(typeCode)) {
			ItemTypeDefinition def = typeService.getItemTypeDefinitions().get(typeCode);

			ret = itemTypeConverter.convert(def);
		}

		return ret;
	}

	@Get(pathMapping = "/models/:typecode", mimeType = "application/javascript", responseTransformer = JsonResponseTransformer.class)
	public Object getModels(Request request, Response response) throws ModelNotFoundException {
		List<? extends Item> models = new ArrayList<>();

		Class<? extends Item> type = typeService.getType(request.params(":typecode"));

		models = modelService.getAll(type);

		return models;
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

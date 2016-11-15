package at.spot.core.management.service.impl;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.service.ConfigurationService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.type.ItemTypeDefinition;
import at.spot.core.management.annotation.Handler;
import at.spot.core.management.data.GenericItemDefinitionData;
import at.spot.core.management.exception.RemoteServiceInitException;
import at.spot.core.management.transformer.JsonResponseTransformer;
import spark.Request;
import spark.Response;

@Service
public class TypeServiceRestEndpoint extends AbstractHttpServiceEndpoint {

	private static final String CONFIG_KEY_PORT = "service.type.rest.port";
	private static final int DEFAULT_PORT = 9000;

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected ConfigurationService configurationService;

	@Autowired
	protected Converter<ItemTypeDefinition, GenericItemDefinitionData> itemTypeConverter;

	@PostConstruct
	@Override
	public void init() throws RemoteServiceInitException {
		loggingService.info(String.format("Initiating remote type REST service on port %s", getPort()));
		super.init();
	}

	@Handler(pathMapping = "/types/", mimeType = "application/javascript", responseTransformer = JsonResponseTransformer.class)
	public Object getTypes(final Request request, final Response response) throws UnknownTypeException {

		final List<GenericItemDefinitionData> types = new ArrayList<>();

		for (final String typeCode : typeService.getItemTypeDefinitions().keySet()) {
			final ItemTypeDefinition def = typeService.getItemTypeDefinition(typeCode);

			final GenericItemDefinitionData d = itemTypeConverter.convert(def);
			types.add(d);
		}

		return types;
	}

	@Handler(pathMapping = "/types/:typecode", mimeType = "application/json", responseTransformer = JsonResponseTransformer.class)
	public Object getType(final Request request, final Response response) throws UnknownTypeException {
		GenericItemDefinitionData ret = null;

		final String typeCode = request.params(":typecode");

		if (StringUtils.isNotBlank(typeCode)) {
			final ItemTypeDefinition def = typeService.getItemTypeDefinitions().get(typeCode);

			ret = itemTypeConverter.convert(def);
		}

		return ret;
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

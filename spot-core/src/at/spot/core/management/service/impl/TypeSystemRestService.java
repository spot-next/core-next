package at.spot.core.management.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.service.ConfigurationService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.type.ItemTypeDefinition;
import at.spot.core.management.annotation.Get;
import at.spot.core.management.data.GenericItemDefinitionData;
import at.spot.core.management.transformer.JsonResponseTransformer;
import spark.Request;
import spark.Response;

@Service
public class TypeSystemRestService extends AbstractHttpService {

	private static final String CONFIG_PORT_KEY = "spot.service.remotetypeservice.port";

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected ConfigurationService configurationService;

	@Autowired
	protected Converter<ItemTypeDefinition, GenericItemDefinitionData> itemTypeConverter;

	@Get(pathMapping = "/types/", mimeType = "application/json", responseTransformer = JsonResponseTransformer.class)
	public Object getTypes(Request request, Response response) {
		List<GenericItemDefinitionData> types = new ArrayList<>();

		for (String typeCode : typeService.getItemTypeDefinitions().keySet()) {
			ItemTypeDefinition def = typeService.getItemTypeDefinition(typeCode);

			GenericItemDefinitionData d = itemTypeConverter.convert(def);
			types.add(d);
		}

		return types;
	}

	@Override
	public int getPort() {
		return configurationService.getInteger(CONFIG_PORT_KEY);
	}
}

package io.spotnext.core.management.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import io.spotnext.core.infrastructure.exception.UnknownTypeException;
import io.spotnext.core.infrastructure.support.ItemTypeDefinition;
import io.spotnext.core.infrastructure.support.MimeType;
import io.spotnext.core.management.annotation.Handler;
import io.spotnext.core.management.annotation.RemoteEndpoint;
import io.spotnext.core.management.converter.Converter;
import io.spotnext.core.management.support.data.GenericItemDefinitionData;
import io.spotnext.core.management.transformer.JsonResponseTransformer;
import spark.Request;
import spark.Response;

@RemoteEndpoint(portConfigKey = "service.typesystem.rest.port", port = 19000, pathMapping = "/v1/types")
public class TypeSystemServiceRestEndpoint extends AbstractRestEndpoint {

	@Autowired
	protected Converter<ItemTypeDefinition, GenericItemDefinitionData> itemTypeConverter;

	@Handler(pathMapping = "/", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
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

	@Handler(pathMapping = "/:typecode", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
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

}
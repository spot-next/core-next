package io.spotnext.core.management.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import io.spotnext.core.infrastructure.exception.UnknownTypeException;
import io.spotnext.core.infrastructure.http.DataResponse;
import io.spotnext.core.infrastructure.support.MimeType;
import io.spotnext.core.management.annotation.Handler;
import io.spotnext.core.management.annotation.RemoteEndpoint;
import io.spotnext.core.management.converter.Converter;
import io.spotnext.core.management.support.BasicAuthenticationFilter;
import io.spotnext.core.management.support.data.GenericItemDefinitionData;
import io.spotnext.core.management.transformer.JsonResponseTransformer;
import io.spotnext.infrastructure.type.ItemTypeDefinition;
import spark.Request;
import spark.Response;

/**
 * <p>
 * TypeSystemServiceRestEndpoint class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@RemoteEndpoint(portConfigKey = "service.typesystem.rest.port", port = 19000, pathMapping = "/v1/types", authenticationFilter = BasicAuthenticationFilter.class)
public class TypeSystemServiceRestEndpoint extends AbstractRestEndpoint {

	@Autowired
	protected Converter<ItemTypeDefinition, GenericItemDefinitionData> itemTypeConverter;

	/**
	 * <p>
	 * getTypes.
	 * </p>
	 *
	 * @param request  a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return a {@link java.util.List} object.
	 * @throws io.spotnext.infrastructure.exception.UnknownTypeException if any.
	 */
	@Handler(pathMapping = "/", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public DataResponse getTypes(final Request request, final Response response)
			throws UnknownTypeException {

		final List<GenericItemDefinitionData> types = new ArrayList<>();

		for (final String typeCode : typeService.getItemTypeDefinitions().keySet()) {
			final ItemTypeDefinition def = typeService.getItemTypeDefinition(typeCode);

			final GenericItemDefinitionData d = itemTypeConverter.convert(def);
			types.add(d);
		}

		return DataResponse.ok().withPayload(types);
	}

	/**
	 * <p>
	 * getType.
	 * </p>
	 *
	 * @param request  a {@link spark.Request} object.
	 * @param response a {@link spark.Response} object.
	 * @return a {@link io.spotnext.core.management.support.data.GenericItemDefinitionData} object.
	 * @throws io.spotnext.infrastructure.exception.UnknownTypeException if any.
	 */
	@Handler(pathMapping = "/:typecode", mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public DataResponse getType(final Request request, final Response response)
			throws UnknownTypeException {

		final String typeCode = request.params(":typecode");

		if (StringUtils.isNotBlank(typeCode)) {
			final ItemTypeDefinition def = typeService.getItemTypeDefinitions().get(StringUtils.lowerCase(typeCode));

			if (def != null) {
				final GenericItemDefinitionData data = itemTypeConverter.convert(def);
				return DataResponse.ok().withPayload(data);
			} else {
				return DataResponse.notFound().withError("error.typecode.unknown", "No type definition found for give typecode");
			}
		}

		return DataResponse.conflict();
	}

}

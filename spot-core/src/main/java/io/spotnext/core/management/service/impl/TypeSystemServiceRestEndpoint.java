package io.spotnext.core.management.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import io.spotnext.core.constant.CoreConstants;
import io.spotnext.core.infrastructure.exception.UnknownTypeException;
import io.spotnext.core.infrastructure.http.DataResponse;
import io.spotnext.core.infrastructure.support.MimeType;
import io.spotnext.core.management.annotation.Handler;
import io.spotnext.core.management.annotation.RemoteEndpoint;
import io.spotnext.core.management.converter.Converter;
import io.spotnext.core.management.support.BasicAuthenticationFilter;
import io.spotnext.core.management.support.data.GenericItemDefinitionData;
import io.spotnext.core.management.support.data.PageablePayload;
import io.spotnext.core.management.transformer.JsonResponseTransformer;
import io.spotnext.core.persistence.query.Pageable;
import io.spotnext.infrastructure.type.ItemTypeDefinition;
import io.spotnext.support.util.ClassUtil;
import io.spotnext.support.util.MiscUtil;
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

		final int page = MiscUtil.positiveIntOrDefault(request.queryParams("page"), CoreConstants.REQUEST_DEFAULT_PAGE);
		final int pageSize = MiscUtil.positiveIntOrDefault(request.queryParams("pageSize"), CoreConstants.REQUEST_DEFAULT_PAGE_SIZE);
		final String sortField = request.queryParams("sort");

		var allTypes = typeService.getItemTypeDefinitions();

		final List<GenericItemDefinitionData> types = allTypes.values().stream() //
				.skip(MiscUtil.positiveIntOrDefault(page - 1, 1) * pageSize) //
				.limit(pageSize) //
				.map(itemTypeConverter::convert) //
				.sorted(getComparator(sortField)) //
				.collect(Collectors.toList());

		final Pageable<GenericItemDefinitionData> pageableData = new PageablePayload<GenericItemDefinitionData>(types, page,
				pageSize, Long.valueOf(allTypes.values().size()));

		return DataResponse.ok().withPayload(pageableData);
	}

	private Comparator<GenericItemDefinitionData> getComparator(String sortField) {
		String[] sort = StringUtils.trimToEmpty(sortField).split(" ");

		final String field = sort.length > 0 ? StringUtils.defaultIfBlank(sort[0], "typeCode") : "typeCode";
		final boolean descending = sort.length > 1 ? "DESC".equals(StringUtils.trimToEmpty(sort[1])) : false;

		Comparator<GenericItemDefinitionData> comparator = new Comparator<>() {
			public int compare(GenericItemDefinitionData o1, GenericItemDefinitionData o2) {
				Object fieldValue1 = ClassUtil.getField(o1, field, true);
				Object fieldValue2 = ClassUtil.getField(o2, field, true);

				if (fieldValue1 instanceof Comparable && fieldValue2 instanceof Comparable) {
					return (descending ? -1 : 1) * ((Comparable) fieldValue1).compareTo(fieldValue2);
				}

				return 0;
			};
		};

		return comparator;
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

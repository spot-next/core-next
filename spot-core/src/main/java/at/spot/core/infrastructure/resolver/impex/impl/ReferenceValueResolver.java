package at.spot.core.infrastructure.resolver.impex.impl;

import java.lang.reflect.Field;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.exception.ValueResolverException;
import at.spot.core.infrastructure.resolver.impex.ImpexValueResolver;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.infrastructure.support.impex.ColumnDefinition;
import at.spot.core.persistence.query.JpqlQuery;
import at.spot.core.persistence.query.QueryResult;
import at.spot.core.persistence.service.QueryService;
import at.spot.core.support.util.ClassUtil;
import at.spot.core.types.Item;

@Service
public class ReferenceValueResolver extends AbstractService implements ImpexValueResolver {

	@Resource
	private TypeService typeService;

	@Resource
	private QueryService queryService;

	@Override
	public <T> T resolve(final String value, final Class<T> targetType, final List<Class<?>> genericArguments,
			final ColumnDefinition columnDefinition) throws ValueResolverException {

		return resolve(value, targetType, genericArguments, columnDefinition.getValueResolutionDescriptor(), 0);
	}

	protected <T> T resolve(final String value, final Class<T> targetType, final List<Class<?>> genericArguments,
			String desc, final int resolutionLevel) throws ValueResolverException {

		if (StringUtils.isBlank(value)) {
			return null;
		}

		// remove spaces in the string, makes it easier to parse
		desc = desc.replace(" ", "");
		final String[] inputParams = value.split(":");

		final List<Node> nodes = parse(new StringCharacterIterator(desc), targetType);
		final QueryDefinition queryDef = new QueryDefinition((Class<Item>) targetType);
		fillQuery(queryDef, (Class<Item>) targetType, nodes.toArray(new Node[0]));

		if (inputParams.length != queryDef.getParamCount()) {
			throw new ValueResolverException("Input values doesn't match expected header column definition.");
		}

		final JpqlQuery<T> qry = new JpqlQuery<>(queryDef.toString(), targetType);

		for (int x = 0; x < queryDef.getParamCount(); x++) {
			qry.addParam("" + x, inputParams[x]);
		}

		final QueryResult<T> result = queryService.query(qry);

		if (result.getResultList().size() > 1) {
			throw new ValueResolverException("Ambiguous results found for given input values.");
		} else if (result.getResultList().size() == 0) {
			throw new ValueResolverException("No results found for given input values.");
		}

		return result.getResultList().get(0);
	}

	private void fillQuery(final QueryDefinition queryDef, final Class<Item> type, final Node... nodes) {
		for (final Node node : nodes) {
			if (node.getNodes().size() > 0) {

				final Field propertyField = ClassUtil.getFieldDefinition(type, node.getPropertyName(), true);
				final Class<?> fieldType = propertyField.getType();

				final String fieldTypeName = fieldType.getSimpleName();

				queryDef.getJoinClauses().add("JOIN " + fieldTypeName + " AS " + fieldTypeName + " ON "
						+ type.getSimpleName() + "." + propertyField.getName() + " = " + fieldTypeName + ".pk ");

				fillQuery(queryDef, (Class<Item>) fieldType, node.getNodes().toArray(new Node[0]));

			} else {
				queryDef.getWhereClauses().add(
						type.getSimpleName() + "." + node.getPropertyName() + " = ?" + queryDef.getNextParamIndex());
			}
		}
	}

	private List<Node> parse(final StringCharacterIterator descIterator, final Class<?> itemType) {
		// I know .. it's not pretty ...
		final List<Node> nodes = new ArrayList<>();

		Node tempNode = null;
		String tempToken = "";

		char c;
		while ((c = descIterator.next()) != CharacterIterator.DONE) {
			// we found a simple leave node
			if (c == ',') {
				if (StringUtils.isNotBlank(tempToken)) {
					tempNode = new Node(tempToken, itemType);
					nodes.add(tempNode);
					tempToken = "";
				} else {
					continue;
				}
			} else if (c == '(') {
				// this is a tree node
				if (StringUtils.isNotBlank(tempToken)) {
					tempNode = new Node(tempToken, itemType);
					nodes.add(tempNode);
					tempToken = "";

					final Field propertyField = ClassUtil.getFieldDefinition(itemType, tempNode.getPropertyName(),
							true);
					final List<Node> children = parse(descIterator, propertyField.getType());

					tempNode.nodes.addAll(children);
				}
			} else if (c == ')') {
				if (StringUtils.isNotBlank(tempToken)) {
					tempNode = new Node(tempToken, itemType);
					nodes.add(tempNode);
				}
				break;
			} else {
				tempToken += c;
			}
		}

		return nodes;
	}

	public static class QueryDefinition {
		final Class<Item> rootType;
		final List<String> joinClauses = new ArrayList<>();
		final List<String> whereClauses = new ArrayList<>();
		int paramCount = 0;

		public QueryDefinition(final Class<Item> rootType) {
			this.rootType = rootType;
		}

		public Class<Item> getRootType() {
			return rootType;
		}

		public List<String> getJoinClauses() {
			return joinClauses;
		}

		public List<String> getWhereClauses() {
			return whereClauses;
		}

		public int getNextParamIndex() {
			return paramCount++;
		}

		public int getParamCount() {
			return paramCount;
		}

		@Override
		public String toString() {
			final String query = "SELECT " + rootType.getSimpleName() + " FROM " + rootType.getSimpleName() + " AS "
					+ rootType.getSimpleName() + " " + joinClauses.stream().collect(Collectors.joining(" ")) + " WHERE "
					+ whereClauses.stream().collect(Collectors.joining(" AND "));

			return query;
		}
	}

	public static class Node {
		private String name;
		private final List<Node> nodes = new ArrayList<>();
		private final Class<?> itemType;

		public Node(final String name, final Class<?> itemType) {
			this.name = name;
			this.itemType = itemType;
		}

		public String getPropertyName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public List<Node> getNodes() {
			return nodes;
		}

		public Class<?> getItemType() {
			return itemType;
		}

		@Override
		public String toString() {
			return name + (nodes.size() > 0
					? "(" + nodes.stream().map(n -> n.toString()).collect(Collectors.joining(",")) + ")"
					: "");
		}
	}
}

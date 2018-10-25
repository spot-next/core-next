package io.spotnext.core.persistence.hibernate.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.persistence.InheritanceType;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.QualifiedNameImpl;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Index;
import org.hibernate.tool.schema.internal.StandardIndexExporter;

/**
 * Appends a unique ID to any index name defined using {@link org.hibernate.annotations.Index}. This fixes duplicate indexes when using
 * {@link InheritanceType#SINGLE_TABLE}.
 */
public class FixedIndexExporter extends StandardIndexExporter {
	private Dialect dialect;

	public FixedIndexExporter(Dialect dialect) {
		super(dialect);
		this.dialect = dialect;
	}

	@Override
	public String[] getSqlCreateStrings(Index index, Metadata metadata) {
		final JdbcEnvironment jdbcEnvironment = metadata.getDatabase().getJdbcEnvironment();
		final String tableName = jdbcEnvironment.getQualifiedObjectNameFormatter().format(
				index.getTable().getQualifiedTableName(),
				dialect);

		String indexNameForCreation;

		if (dialect.qualifyIndexName()) {
			indexNameForCreation = jdbcEnvironment.getQualifiedObjectNameFormatter().format(
					new QualifiedNameImpl(
							index.getTable().getQualifiedTableName().getCatalogName(),
							index.getTable().getQualifiedTableName().getSchemaName(),
							jdbcEnvironment.getIdentifierHelper().toIdentifier(index.getQuotedName(dialect))),
					jdbcEnvironment.getDialect());
		} else {
			indexNameForCreation = index.getName();
		}

		indexNameForCreation += "_" + UUID.randomUUID().toString().replace("-", "");

		final StringBuilder buf = new StringBuilder()
				.append("create index ")
				.append(indexNameForCreation)
				.append(" on ")
				.append(tableName)
				.append(" (");

		boolean first = true;
		final Iterator<Column> columnItr = index.getColumnIterator();
		final Map<Column, String> columnOrderMap = index.getColumnOrderMap();
		while (columnItr.hasNext()) {
			final Column column = columnItr.next();
			if (first) {
				first = false;
			} else {
				buf.append(", ");
			}
			buf.append((column.getQuotedName(dialect)));
			if (columnOrderMap.containsKey(column)) {
				buf.append(" ").append(columnOrderMap.get(column));
			}
		}
		buf.append(")");
		return new String[] { buf.toString() };
	}
}

package io.spotnext.core.persistence.hibernate.impl;

import org.hibernate.dialect.PostgreSQL95Dialect;
import org.hibernate.mapping.Index;
import org.hibernate.tool.schema.spi.Exporter;

public class PostgresDialect extends PostgreSQL95Dialect {
	private Exporter<Index> indexExporter = new FixedIndexExporter(this);

	@Override
	public Exporter<Index> getIndexExporter() {
		return indexExporter;
	}
}

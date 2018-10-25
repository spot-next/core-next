package io.spotnext.core.persistence.hibernate.impl;

import org.hibernate.dialect.HSQLDialect;
import org.hibernate.mapping.Index;
import org.hibernate.tool.schema.spi.Exporter;

/**
 * Needed to inject custom {@link FixedIndexExporter}.
 */
public class HsqlDialect extends HSQLDialect {
	
	private Exporter<Index> indexExporter = new FixedIndexExporter(this);
	
	@Override
	public Exporter<Index> getIndexExporter() {
		return indexExporter;
	}
}

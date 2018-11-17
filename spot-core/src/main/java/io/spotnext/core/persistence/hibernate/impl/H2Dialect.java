package io.spotnext.core.persistence.hibernate.impl;

import org.hibernate.mapping.Index;
import org.hibernate.tool.schema.spi.Exporter;

//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Needed to inject custom {@link FixedIndexExporter}.
 */
//@SuppressFBWarnings(value = "NM_SAME_SIMPLE_NAME_AS_SUPERCLASS")
public class H2Dialect extends org.hibernate.dialect.H2Dialect {

	private Exporter<Index> indexExporter = new FixedIndexExporter(this);

	@Override
	public Exporter<Index> getIndexExporter() {
		return indexExporter;
	}
}

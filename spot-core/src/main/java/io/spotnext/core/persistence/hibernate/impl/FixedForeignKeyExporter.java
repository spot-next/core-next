package io.spotnext.core.persistence.hibernate.impl;

import org.hibernate.boot.Metadata;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.tool.schema.internal.StandardForeignKeyExporter;
import org.hibernate.tool.schema.spi.Exporter;

public class FixedForeignKeyExporter extends StandardForeignKeyExporter implements Exporter<ForeignKey> {

	public FixedForeignKeyExporter(final Dialect dialect) {
		super(dialect);
	}

	@Override
	public String[] getSqlCreateStrings(final ForeignKey exportable, final Metadata metadata) {
		return null;
	}

	@Override
	public String[] getSqlDropStrings(final ForeignKey exportable, final Metadata metadata) {
		return super.getSqlDropStrings(exportable, metadata);
	}

}

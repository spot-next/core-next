package io.spotnext.core.persistence.hibernate.impl;

import org.hibernate.dialect.InnoDBStorageEngine;
import org.hibernate.dialect.MySQLStorageEngine;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.tool.schema.spi.Exporter;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Needed to inject custom {@link FixedIndexExporter}.
 */
@SuppressFBWarnings(value = "NM_SAME_SIMPLE_NAME_AS_SUPERCLASS")
public class MysqlDialect extends org.hibernate.dialect.MySQL5Dialect {

	@Override
	public Exporter<ForeignKey> getForeignKeyExporter() {
		return new FixedForeignKeyExporter(this);
	}

	@Override
	public String getAddForeignKeyConstraintString(final String constraintName, final String foreignKeyDefinition) {
		return super.getAddForeignKeyConstraintString(constraintName, foreignKeyDefinition);
	}

	@Override
	public String getAddForeignKeyConstraintString(final String constraintName, final String[] foreignKey, final String referencedTable,
			final String[] primaryKey,
			final boolean referencesPrimaryKey) {

		return super.getAddForeignKeyConstraintString(constraintName, foreignKey, referencedTable, primaryKey, referencesPrimaryKey);
	}

	@Override
	protected MySQLStorageEngine getDefaultMySQLStorageEngine() {
		return InnoDBStorageEngine.INSTANCE;
	}
}

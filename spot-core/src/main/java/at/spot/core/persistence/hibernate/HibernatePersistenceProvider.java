package at.spot.core.persistence.hibernate;

import javax.persistence.spi.PersistenceProvider;

import org.hibernate.boot.model.relational.Database;

public interface HibernatePersistenceProvider extends PersistenceProvider {

	/**
	 * Returns the Database instance used during the metadata collection process.
	 */
	Database getDatabase();

}

package at.spot.core.persistence.service.impl.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;

import org.hibernate.boot.model.relational.Database;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.springframework.orm.jpa.persistenceunit.SmartPersistenceUnitInfo;

/**
 * Copy of
 * org.springframework.orm.jpa.vendor.SpringHibernateJpaPersistenceProvider.
 */
public class HibernatePersistenceProviderImpl extends HibernatePersistenceProvider {

	protected EntityManagerFactoryBuilderImpl builder;

	@Override
	@SuppressWarnings("rawtypes")
	public EntityManagerFactory createContainerEntityManagerFactory(final PersistenceUnitInfo info,
			final Map properties) {
		final List<String> mergedClassesAndPackages = new ArrayList<>(info.getManagedClassNames());
		if (info instanceof SmartPersistenceUnitInfo) {
			mergedClassesAndPackages.addAll(((SmartPersistenceUnitInfo) info).getManagedPackages());
		}
		return new EntityManagerFactoryBuilderImpl(new PersistenceUnitInfoDescriptor(info) {
			@Override
			public List<String> getManagedClassNames() {
				return mergedClassesAndPackages;
			}
		}, properties).build();
	}

	public Database getDatabase() {
		return builder.getMetadata().getDatabase();
	}

}

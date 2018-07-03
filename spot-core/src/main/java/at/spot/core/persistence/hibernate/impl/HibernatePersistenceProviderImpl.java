package at.spot.core.persistence.hibernate.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;

import org.hibernate.boot.model.relational.Database;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.springframework.orm.jpa.persistenceunit.SmartPersistenceUnitInfo;

import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.support.spring.Registry;

/**
 * Copy of
 * org.springframework.orm.jpa.vendor.SpringHibernateJpaPersistenceProvider.
 */
public class HibernatePersistenceProviderImpl extends HibernatePersistenceProvider
		implements at.spot.core.persistence.hibernate.HibernatePersistenceProvider {

	protected EntityManagerFactoryBuilderImpl builder;
	protected LoggingService logginService;

	@Override
	@SuppressWarnings("rawtypes")
	public EntityManagerFactory createContainerEntityManagerFactory(final PersistenceUnitInfo info,
			final Map properties) {

		final List<String> mergedClassesAndPackages = new ArrayList<>(info.getManagedClassNames());

		if (info instanceof SmartPersistenceUnitInfo) {
			mergedClassesAndPackages.addAll(((SmartPersistenceUnitInfo) info).getManagedPackages());
		}

		properties.put("hibernate.integrator_provider",
				(IntegratorProvider) () -> Collections.singletonList(MetadataExtractorIntegrator.INSTANCE));

		builder = new EntityManagerFactoryBuilderImpl(new PersistenceUnitInfoDescriptor(info) {
			@Override
			public List<String> getManagedClassNames() {
				return mergedClassesAndPackages;
			}
		}, properties);

		try {
			return builder.build();
		} catch (final Exception e) {
			getLogginService().warn("The type system is not initialized!");
			Registry.shutdown();
			return null;
		}
	}

	public LoggingService getLogginService() {
		if (logginService == null) {
			logginService = Registry.getLoggingService();
		}

		return logginService;
	}

	@Override
	public Database getDatabase() {
		return builder.getMetadata().getDatabase();
	}

}

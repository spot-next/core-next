package io.spotnext.core.persistence.hibernate.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.persistenceunit.SmartPersistenceUnitInfo;

/**
 * Copy of
 * org.springframework.orm.jpa.vendor.SpringHibernateJpaPersistenceProvider.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class HibernatePersistenceProviderImpl extends HibernatePersistenceProvider {

	private static final Logger LOG = LoggerFactory.getLogger(HibernatePersistenceProviderImpl.class);

	protected EntityManagerFactoryBuilderImpl builder;

	/** {@inheritDoc} */
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

		builder = new EntityManagerFactoryBuilderImpl(
				new SpotPersistenceUnitInfoDescriptor(info, mergedClassesAndPackages), properties);

		return builder.build();
	}

	public static class SpotPersistenceUnitInfoDescriptor extends PersistenceUnitInfoDescriptor {

		final List<String> mergedClassesAndPackages;

		public SpotPersistenceUnitInfoDescriptor(PersistenceUnitInfo persistenceUnitInfo,
				List<String> mergedClassesAndPackages) {
			super(persistenceUnitInfo);
			this.mergedClassesAndPackages = mergedClassesAndPackages;
		}

		@Override
		public List<String> getManagedClassNames() {
			return mergedClassesAndPackages;
		}
	}
}

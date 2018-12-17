package io.spotnext.core.persistence.hibernate.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;

import org.hibernate.boot.internal.MetadataBuilderImpl;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.persistenceunit.SmartPersistenceUnitInfo;

import io.spotnext.support.util.ClassUtil;

/**
 * Copy of org.springframework.orm.jpa.vendor.SpringHibernateJpaPersistenceProvider.
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

		// this is a hacky way to use JPA bootstrap but then set it back to "native"
		// this is currently necessary as it is not possible to subclass the factory builder and inject other settings
		// the purpose of this is to bypass a misbehaviour of the JPA implementation during deletion of still-referenced instances
		// native hibernate will fail with an "re-saved by cascade" exception, JPA will just silently fail and not delete anything ...
		MetadataBuilderImpl metamodelBuilder = (MetadataBuilderImpl) ClassUtil.getField(builder, "metamodelBuilder", true);
		BootstrapContext context = metamodelBuilder.getBootstrapContext();
		ClassUtil.setField(context, "isJpaBootstrap", Boolean.FALSE);

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

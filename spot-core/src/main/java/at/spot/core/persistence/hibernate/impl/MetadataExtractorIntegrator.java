package at.spot.core.persistence.hibernate.impl;

import java.util.Collections;
import java.util.Map;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public class MetadataExtractorIntegrator implements org.hibernate.integrator.spi.Integrator {
	public static final MetadataExtractorIntegrator INSTANCE = new MetadataExtractorIntegrator();

	private Metadata metadata = null;
	private SessionFactoryImplementor sessionFactory = null;
	private SessionFactoryServiceRegistry serviceRegistry = null;

	@Override
	public void integrate(final Metadata metadata, final SessionFactoryImplementor sessionFactory,
			final SessionFactoryServiceRegistry serviceRegistry) {

		this.sessionFactory = sessionFactory;
		this.metadata = metadata;
		this.serviceRegistry = serviceRegistry;
	}

	public Map<String, Object> getProperties() {
		if (this.sessionFactory != null) {
			return this.sessionFactory.getProperties();
		}

		return Collections.emptyMap();
	}

	@Override
	public void disintegrate(final SessionFactoryImplementor sessionFactory,
			final SessionFactoryServiceRegistry serviceRegistry) {
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(final Metadata metadata) {
		this.metadata = metadata;
	}

	public Database getDatabase() {
		return metadata.getDatabase();
	}

	public SessionFactoryImplementor getSessionFactory() {
		return sessionFactory;
	}

	public SessionFactoryServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

}
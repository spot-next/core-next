package at.spot.core.persistence.hibernate.impl;

import java.util.Map;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class MetadataExtractorIntegrator implements org.hibernate.integrator.spi.Integrator {
	public static final MetadataExtractorIntegrator INSTANCE = new MetadataExtractorIntegrator();

	private Metadata metadata;
	private SessionFactoryImplementor sessionFactory;
	private SessionFactoryServiceRegistry serviceRegistry;

	@Override
	public void integrate(final Metadata metadata, final SessionFactoryImplementor sessionFactory,
			final SessionFactoryServiceRegistry serviceRegistry) {

		this.sessionFactory = sessionFactory;
		this.metadata = metadata;
		this.serviceRegistry = serviceRegistry;
	}

	public Map<String, Object> getProperties() {
		return this.sessionFactory.getProperties();
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
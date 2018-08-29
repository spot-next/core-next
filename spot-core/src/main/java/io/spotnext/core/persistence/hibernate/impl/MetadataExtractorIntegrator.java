package io.spotnext.core.persistence.hibernate.impl;

import java.util.Collections;
import java.util.Map;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * <p>MetadataExtractorIntegrator class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public class MetadataExtractorIntegrator implements org.hibernate.integrator.spi.Integrator {
	/** Constant <code>INSTANCE</code> */
	public static final MetadataExtractorIntegrator INSTANCE = new MetadataExtractorIntegrator();

	private Metadata metadata = null;
	private SessionFactoryImplementor sessionFactory = null;
	private SessionFactoryServiceRegistry serviceRegistry = null;

	/** {@inheritDoc} */
	@Override
	public void integrate(final Metadata metadata, final SessionFactoryImplementor sessionFactory,
			final SessionFactoryServiceRegistry serviceRegistry) {

		this.sessionFactory = sessionFactory;
		this.metadata = metadata;
		this.serviceRegistry = serviceRegistry;
	}

	/**
	 * <p>getProperties.</p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public Map<String, Object> getProperties() {
		if (this.sessionFactory != null) {
			return this.sessionFactory.getProperties();
		}

		return Collections.emptyMap();
	}

	/** {@inheritDoc} */
	@Override
	public void disintegrate(final SessionFactoryImplementor sessionFactory,
			final SessionFactoryServiceRegistry serviceRegistry) {
	}

	/**
	 * <p>Getter for the field <code>metadata</code>.</p>
	 *
	 * @return a {@link org.hibernate.boot.Metadata} object.
	 */
	public Metadata getMetadata() {
		return metadata;
	}

	/**
	 * <p>Setter for the field <code>metadata</code>.</p>
	 *
	 * @param metadata a {@link org.hibernate.boot.Metadata} object.
	 */
	public void setMetadata(final Metadata metadata) {
		this.metadata = metadata;
	}

	/**
	 * <p>getDatabase.</p>
	 *
	 * @return a {@link org.hibernate.boot.model.relational.Database} object.
	 */
	public Database getDatabase() {
		return metadata.getDatabase();
	}

	/**
	 * <p>Getter for the field <code>sessionFactory</code>.</p>
	 *
	 * @return a {@link org.hibernate.engine.spi.SessionFactoryImplementor} object.
	 */
	public SessionFactoryImplementor getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * <p>Getter for the field <code>serviceRegistry</code>.</p>
	 *
	 * @return a {@link org.hibernate.service.spi.SessionFactoryServiceRegistry} object.
	 */
	public SessionFactoryServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

}

package at.spot.core.persistence.hibernate.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.type.LongType;
import org.springframework.beans.factory.annotation.Autowired;

import at.spot.core.model.Item;
import at.spot.core.persistence.hibernate.HibernatePersistenceProvider;
import at.spot.core.persistence.service.impl.AbstractSerialNumberGeneratorService;
import at.spot.itemtype.core.UniqueIdItem;

public class HibernateSerialNumberGeneratorService extends AbstractSerialNumberGeneratorService
		implements IdentifierGenerator {

	protected static final String SEQUENCE_PREFIX = "SEQ_";

	@Resource
	protected HibernatePersistenceService persistenceService;

	@Autowired
	protected HibernatePersistenceProvider persistenceProvider;

	protected String sequenceCallSyntax;
	protected Dialect dialect;

	protected Map<Class<?>, SequenceStyleGenerator> generators = new HashMap<>();

	@Override
	@PostConstruct
	public void setup() {
		super.setup();

		final JdbcEnvironment jdbcEnvironment = persistenceService.getSessionFactory().getSessionFactoryOptions()
				.getServiceRegistry().getService(JdbcEnvironment.class);
		dialect = jdbcEnvironment.getDialect();
	}

	@Override
	protected <T extends Item> Long getNextSerialNumber(final T item) {
		return (Long) generate((SharedSessionContractImplementor) persistenceService.getSession(), item);
	}

	@Override
	public Serializable generate(final SharedSessionContractImplementor session, final Object obj)
			throws HibernateException {

		if (obj instanceof UniqueIdItem) {
			final UniqueIdItem identifiable = (UniqueIdItem) obj;
			final Serializable id = identifiable.getId();

			if (id != null) {
				return id;
			}

			final Serializable val = getNextVal(session, obj.getClass());

			return val;
		}

		return null;
	}

	protected Serializable getNextVal(final SharedSessionContractImplementor session, final Class<?> entityClass) {
		final Serializable id = getGenerator(session, entityClass).generate(session, null);

		return id;
	}

	protected SequenceStyleGenerator getGenerator(final SharedSessionContractImplementor session,
			final Class<?> entityClass) {
		SequenceStyleGenerator generator = generators.get(entityClass);

		if (generator == null) {
			final Properties props = new Properties();
			props.putAll(persistenceService.getEntityManagerFactory().getProperties());
			props.put(IdentifierGenerator.JPA_ENTITY_NAME, entityClass.getName());
			props.put("hibernate.id.new_generator_mappings", "false");

			generator = new SequenceStyleGenerator();
			generator.configure(LongType.INSTANCE, props,
					persistenceService.getSessionFactory().getSessionFactoryOptions().getServiceRegistry());
			generator.registerExportables(persistenceProvider.getDatabase());

			generators.put(entityClass, generator);
		}

		return generator;
	}
}

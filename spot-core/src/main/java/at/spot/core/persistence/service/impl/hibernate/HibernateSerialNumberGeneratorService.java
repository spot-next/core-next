package at.spot.core.persistence.service.impl.hibernate;

import java.io.Serializable;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import at.spot.core.model.Item;
import at.spot.core.persistence.service.impl.AbstractSerialNumberGeneratorService;
import at.spot.itemtype.core.UniqueIdItem;

public class HibernateSerialNumberGeneratorService extends AbstractSerialNumberGeneratorService
		implements IdentifierGenerator, Configurable {

	protected static final String SEQUENCE_PREFIX = "SEQ_";

	@Resource
	protected HibernatePersistenceService persistenceService;

	protected String sequenceCallSyntax;
	protected Dialect dialect;

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

			return 0;
		}

		return null;
	}

	protected Serializable getNextVal(final SharedSessionContractImplementor session, final Class<?> entityClass) {
		final Type entityType = session.getFactory().getTypeHelper().entity(entityClass);
		final String entitySequenceName = SEQUENCE_PREFIX + entityType.getName();

		final Properties props = new Properties();
		props.putAll(persistenceService.getEntityManagerFactory().getProperties());

		final SequenceStyleGenerator seqGenerator = new SequenceStyleGenerator();
		seqGenerator.configure(entityType, props,
				persistenceService.getSessionFactory().getSessionFactoryOptions().getServiceRegistry());
		// seqGenerator.registerExportables();

		final Serializable id = seqGenerator.generate(session, null);

		return id;
	}

	@Override
	public void configure(final Type type, final Properties params, final ServiceRegistry serviceRegistry)
			throws MappingException {
		// TODO Auto-generated method stub

	}
}

package io.spotnext.core.persistence.hibernate.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnit;
import javax.persistence.Subgraph;
import javax.persistence.TransactionRequiredException;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.CacheMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.stat.Statistics;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.schema.internal.ExceptionHandlerLoggedImpl;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.hibernate.tool.schema.spi.SchemaManagementTool;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.hibernate.tool.schema.spi.SchemaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionSynchronizationManager;

//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.annotation.logging.Log;
import io.spotnext.core.infrastructure.exception.ModelNotFoundException;
import io.spotnext.core.infrastructure.exception.ModelSaveException;
import io.spotnext.core.infrastructure.exception.UnknownTypeException;
import io.spotnext.core.infrastructure.service.ConfigurationService;
import io.spotnext.core.infrastructure.service.ValidationService;
import io.spotnext.core.infrastructure.support.LogLevel;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.core.persistence.exception.ModelNotUniqueException;
import io.spotnext.core.persistence.exception.QueryException;
import io.spotnext.core.persistence.query.ModelQuery;
import io.spotnext.core.persistence.query.SortOrder;
import io.spotnext.core.persistence.query.SortOrder.OrderDirection;
import io.spotnext.core.persistence.service.TransactionService;
import io.spotnext.core.persistence.service.impl.AbstractPersistenceService;
import io.spotnext.infrastructure.annotation.Property;
import io.spotnext.infrastructure.type.Item;
import io.spotnext.infrastructure.type.ItemTypePropertyDefinition;
import io.spotnext.support.util.ClassUtil;

/**
 * <p>
 * HibernatePersistenceService class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@DependsOn("typeService")
//@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
public class HibernatePersistenceService extends AbstractPersistenceService {

	@Value("${hibernate.jdbc.batch_size:}")
	private int jdbcBatchSize = 100;

	protected MetadataExtractorIntegrator metadataIntegrator = MetadataExtractorIntegrator.INSTANCE;

	@PersistenceUnit
	protected EntityManagerFactory entityManagerFactory;
	protected TransactionService transactionService;

	@Autowired
	protected ValidationService validationService;

	/**
	 * <p>
	 * Constructor for HibernatePersistenceService.
	 * </p>
	 *
	 * @param entityManagerFactory a {@link javax.persistence.EntityManagerFactory} object.
	 * @param transactionService   a {@link io.spotnext.core.persistence.service.TransactionService} object.
	 * @param configurationService a {@link io.spotnext.infrastructure.service.ConfigurationService} object.
	 * @param loggingService       a {@link io.spotnext.infrastructure.service.LoggingService} object.
	 */
	@Autowired
	public HibernatePersistenceService(EntityManagerFactory entityManagerFactory, TransactionService transactionService,
			ConfigurationService configurationService) {

		this.entityManagerFactory = entityManagerFactory;
		this.transactionService = transactionService;
		this.configurationService = configurationService;

		if (configurationService.getBoolean("core.setup.typesystem.initialize", false)) {
			Logger.info("Initializing type system schema ...");

			final SchemaExport schemaExport = new SchemaExport();
			schemaExport.setHaltOnError(true);
			schemaExport.setFormat(true);
			schemaExport.setDelimiter(";");
			schemaExport.setOutputFile("db-schema.sql");

			try {
				// TODO will most likely fail, implement a pure JDBC "drop
				// database" approach?
				schemaExport.drop(EnumSet.of(TargetType.DATABASE), metadataIntegrator.getMetadata());
			} catch (final Exception e) {
				Logger.warn("Could not drop type system schema.");
			}

			schemaExport.createOnly(EnumSet.of(TargetType.DATABASE), metadataIntegrator.getMetadata());
		}

		if (configurationService.getBoolean("core.setup.typesystem.update", false)) {
			Logger.info("Updating type system schema ...");

			final SchemaUpdate schemaExport = new SchemaUpdate();
			schemaExport.setHaltOnError(true);
			schemaExport.setFormat(true);
			schemaExport.setDelimiter(";");
			schemaExport.setOutputFile("db-schema.sql");
			schemaExport.execute(EnumSet.of(TargetType.DATABASE), metadataIntegrator.getMetadata());
		}

		// validate schema
		final SchemaManagementTool tool = metadataIntegrator.getServiceRegistry()
				.getService(SchemaManagementTool.class);

		try {
			final SchemaValidator validator = tool.getSchemaValidator(entityManagerFactory.getProperties());
			validator.doValidation(metadataIntegrator.getMetadata(), SchemaManagementToolCoordinator
					.buildExecutionOptions(entityManagerFactory.getProperties(), ExceptionHandlerLoggedImpl.INSTANCE));

			Logger.debug("Type system schema seems to be OK");

		} catch (final SchemaManagementException e) {
			// currently hibernate throws a validation exception for float values that are being created as doubles ...
			// see https://hibernate.atlassian.net/browse/HHH-8690
			// so we hide that message in case we just did an initialization, otherwise it would look confusing in the logs
			if (!configurationService.getBoolean("core.setup.typesystem.initialize", false)) {
				Logger.warn("Type system schema needs to be initialized/updated");
			}
		}

		if (configurationService.getBoolean("cleantypesystem", false)) {
			Logger.info("Cleaning type system ... (not yet implemented)");
		}

		Logger.info(String.format("Persistence service initialized"));
	}

	/** {@inheritDoc} */
	// @SuppressFBWarnings("REC_CATCH_EXCEPTION")
	@Override
	public <T> List<T> query(final io.spotnext.core.persistence.query.JpqlQuery<T> sourceQuery) throws QueryException {
		bindSession();

		try {
			return transactionService.execute(() -> {
				List<T> results = null;

				final Session session = getSession();

				// if this is an item type, we just load the entities
				// if it is a "primitive" natively supported type we can also
				// just let hibernate do the work
				if (Item.class.isAssignableFrom(sourceQuery.getResultClass())
						|| NATIVE_DATATYPES.contains(sourceQuery.getResultClass())) {

					Query<T> query = null;

					try {
						query = session.createQuery(sourceQuery.getQuery(), sourceQuery.getResultClass());
					} catch (final Exception e) {
						throw new QueryException("Could not parse query", e);
					}

					// query.setReadOnly(true).setHint(QueryHints.HINT_CACHEABLE,
					// true);

					setCacheSettings(session, sourceQuery, query);
					setFetchSubGraphsHint(session, sourceQuery, query);
					setParameters(sourceQuery.getParams(), query);
					setPagination(query, sourceQuery.getPage(), sourceQuery.getPageSize());
					results = query.getResultList();

				} else {
					// otherwise we load each value into a list of tuples
					// in that case the selected columns need to be aliased in
					// case
					// the given result
					// type has no constructor that exactly matches the returned
					// columns' types, as
					// otherwise we cannot map the row values to properties.

					final Query<Tuple> query;
					if (Void.class.isAssignableFrom(sourceQuery.getResultClass())) {
						query = session.createQuery(sourceQuery.getQuery());
					} else {
						query = session.createQuery(sourceQuery.getQuery(), Tuple.class);
					}

					// optimize query query.setReadOnly(true).setHint(QueryHints.HINT_CACHEABLE,
					// true);

					setParameters(sourceQuery.getParams(), query);
					setPagination(query, sourceQuery.getPage(), sourceQuery.getPageSize());

					// only try to load results if the result type is not Void
					if (Void.class.isAssignableFrom(sourceQuery.getResultClass())) {
						query.executeUpdate();
						session.flush();
						if (sourceQuery.isClearCaches()) {
							session.clear();
						}
					} else {
						final List<Tuple> resultList = query.list();
						results = new ArrayList<>();

						for (final Tuple t : resultList) {
							// first try to create the pojo using a constructor
							// that matches the result's column types

							final List<Object> values = t.getElements().stream().map(e -> t.get(e))
									.collect(Collectors.toList());
							Optional<T> pojo = ClassUtil.instantiate(sourceQuery.getResultClass(), values.toArray());

							// if the POJO can't be instantiated, we try to
							// create it manually and inject the data using
							// reflection for this to work, each selected column
							// has to have the same alias as the pojo's
							// property!
							if (!pojo.isPresent()) {
								final Optional<T> obj = ClassUtil.instantiate(sourceQuery.getResultClass());

								if (obj.isPresent()) {
									final Object o = obj.get();
									t.getElements().stream()
											.forEach(el -> ClassUtil.setField(o, el.getAlias(), t.get(el.getAlias())));
								}

								pojo = obj;
							}

							if (pojo.isPresent()) {
								results.add(pojo.get());
							} else {
								throw new InstantiationException(String.format("Could not instantiate result type '%s'",
										sourceQuery.getResultClass()));
							}
						}
					}
				}

				return results;
			});
		} catch (final QueryException e) {
			throw e;
		} catch (final Exception e) {
			throw new QueryException(String.format("Could not execute query '%s'", sourceQuery.getQuery()), e);
		}
	}

	protected <T, Q extends io.spotnext.core.persistence.query.Query<T>> void setCacheSettings(final Session session,
			final Q sourceQuery, final TypedQuery<T> query) {

		CacheMode cacheMode = CacheMode.NORMAL;

		if (!sourceQuery.isCachable() && !sourceQuery.isIgnoreCache()) {
			cacheMode = CacheMode.GET;
		} else if (!sourceQuery.isCachable() && sourceQuery.isIgnoreCache()) {
			cacheMode = CacheMode.IGNORE;
		} else if (sourceQuery.isCachable() && sourceQuery.isIgnoreCache()) {
			cacheMode = CacheMode.PUT;
		}

		session.setCacheMode(cacheMode);
//		query.setHint("org.hibernate.cacheable", sourceQuery.isCachable());
		query.setHint("javax.persistence.cache.retrieveMode", sourceQuery.isIgnoreCache() ? CacheRetrieveMode.BYPASS : CacheRetrieveMode.USE);
	}

	protected <T, Q extends io.spotnext.core.persistence.query.Query<T>> void setFetchSubGraphsHint(
			final Session session, final Q sourceQuery, final TypedQuery<T> query) throws UnknownTypeException {

		// TODO what about fetchgraph?

		final List<String> fetchSubGraphs = new ArrayList<>();

		if (sourceQuery.isEagerFetchRelations()) {
			final Map<String, ItemTypePropertyDefinition> props = typeService
					.getItemTypeProperties(typeService.getTypeCodeForClass((Class<Item>) sourceQuery.getResultClass()));

			// add all properties
			final List<String> validProperties = props.values().stream() //
					.filter(p -> Item.class.isAssignableFrom(p.getReturnType()) || p.getRelationDefinition() != null) //
					.map(p -> p.getName()) //
					.collect(Collectors.toList());
			fetchSubGraphs.addAll(validProperties);
		} else if (sourceQuery.getEagerFetchRelationProperties().size() > 0) {
			fetchSubGraphs.addAll(sourceQuery.getEagerFetchRelationProperties());
		}

		if (fetchSubGraphs.size() > 0) {
			if (!Item.class.isAssignableFrom(sourceQuery.getResultClass())) {
				Logger.debug("Fetch sub graphs can only be used for item queries - ignoring");
				return;
			}

			final EntityGraph<T> graph = session.createEntityGraph(sourceQuery.getResultClass());

			for (final String subgraph : fetchSubGraphs) {
				final Subgraph<?> itemGraph = graph.addSubgraph(subgraph);
			}

			query.setHint("javax.persistence.loadgraph", graph);
		}
	}

	protected <T> void setParameters(final Map<String, Object> params, final Query<T> query) {
		for (final Map.Entry<String, Object> entry : params.entrySet()) {
			if (NumberUtils.isCreatable(entry.getKey())) {
				query.setParameter(Integer.parseInt(entry.getKey()), entry.getValue());
			} else {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
	}

	protected void setPagination(final javax.persistence.Query query, final int page, final int pageSize) {
		if (pageSize > 0) {
			query.setFirstResult((page > 0 ? page - 1 : 0) * pageSize);
			query.setMaxResults(pageSize);
		}
	}

	/** {@inheritDoc} */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true, executionTimeThreshold = 100)
	@Override
	public <T extends Item> void save(final List<T> items) throws ModelSaveException, ModelNotUniqueException {
		bindSession();

		try {
			transactionService.execute(() -> {
				final Session session = getSession();
				int i = 0;

				try {
					for (final T item : items) {
						if (item.getVersion() == -1) {
							session.save(item);
						} else {
							session.saveOrUpdate(item);
						}

						// use same as the JDBC batch size
						if (i >= jdbcBatchSize && i % jdbcBatchSize == 0) {
							// flush a batch of inserts and release memory:
							session.flush();
						}
						i++;
					}

					// this is needed, otherwise saved entities are not
					session.flush();
					items.stream().forEach(o -> session.evict(o));
				} catch (final ValidationException e) {
					final String message;
					if (e instanceof ConstraintViolationException) {
						message = validationService
								.convertToReadableMessage(((ConstraintViolationException) e).getConstraintViolations());
					} else {
						message = e.getMessage();
					}

					throw new ModelSaveException(message);
				} catch (final DataIntegrityViolationException | TransactionRequiredException
						| IllegalArgumentException e) {

					throw new ModelSaveException("Could not save given items: " + e.getMessage(), e);

				} catch (final PersistenceException e) {
					final Throwable rootCause = ExceptionUtils.getRootCause(e);
					final String rootCauseMessage = rootCause != null ? rootCause.getMessage() : e.getMessage();

					throw new ModelSaveException(rootCauseMessage, e);
				}

				return null;
			});
		} catch (final TransactionException e) {
			if (e.getCause() instanceof ModelSaveException) {
				throw (ModelSaveException) e.getCause();
			} else if (e.getCause() instanceof ModelNotUniqueException) {
				throw (ModelNotUniqueException) e.getCause();
			} else {
				throw e;
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> T load(final Class<T> type, final long pk, boolean returnProxy) throws ModelNotFoundException {
		bindSession();

		try {
			return transactionService.execute(() -> {
				T item = returnProxy ? getSession().load(type, pk) : getSession().get(type, pk);
				return item;
			});
		} catch (final TransactionException e) {
			if (e.getCause() instanceof ModelNotFoundException) {
				throw (ModelNotFoundException) e.getCause();
			} else {
				throw e;
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> void refresh(final List<T> items) throws ModelNotFoundException {
		bindSession();

		try {
			transactionService.execute(() -> {
				for (final T item : items) {
					try {
						if (attach(item)) {
							getSession().refresh(item, LockMode.NONE);
						}
					} catch (DataIntegrityViolationException | HibernateException | TransactionRequiredException | IllegalArgumentException
							| EntityNotFoundException e) {
						throw new ModelNotFoundException(
								String.format("Could not refresh item with pk=%s.", item.getPk()), e);
					}
				}

				return null;
			});
		} catch (final TransactionException e) {
			if (e.getCause() instanceof ModelNotFoundException) {
				throw (ModelNotFoundException) e.getCause();
			} else {
				throw e;
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> boolean attach(final T item) throws ModelNotFoundException {
		bindSession();

		try {
			// ignore unpersisted or already attached items
			if (isAttached(item)) {
				return true;
			}

			getSession().load(item, item.getPk());
		} catch (HibernateException | TransactionRequiredException | IllegalArgumentException
				| EntityNotFoundException e) {
			throw new ModelNotFoundException(
					String.format("Could not attach item with pk=%s to the current session.", item.getPk()), e);
		}

		return false;
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> List<T> load(final ModelQuery<T> sourceQuery) {

		bindSession();

		return transactionService.execute(() -> {

			final Session session = getSession();
			final CriteriaBuilder builder = session.getCriteriaBuilder();

			final CriteriaQuery<T> cq = builder.createQuery(sourceQuery.getResultClass());
			final Root<T> queryResultType = cq.from(sourceQuery.getResultClass());
			CriteriaQuery<T> itemSelect = cq.select(queryResultType);

			// check if we have to perform a separate query for pagination
			// hibernate can't handle pagination together with FETCH JOINs!
			boolean isPkQueryForPaginationNeeded = sourceQuery.getPageSize() > 0
					&& (sourceQuery.getEagerFetchRelationProperties().size() > 0 || sourceQuery.isEagerFetchRelations());
			boolean isSearchParametersDefined = MapUtils.isNotEmpty(sourceQuery.getSearchParameters());

			Predicate whereClause = null;

			if (isSearchParametersDefined) {
				whereClause = builder.conjunction();

				for (final Map.Entry<String, Object> entry : sourceQuery.getSearchParameters().entrySet()) {
					if (entry.getValue() instanceof Item && !((Item) entry.getValue()).isPersisted()) {
						throw new PersistenceException(String.format(
								"Passing non-persisted item as search param '%s' is not supported.", entry.getKey()));
					}

					whereClause = builder.and(whereClause, builder.equal(queryResultType.get(entry.getKey()), entry.getValue()));
				}
			}

			// always order by last created date and THEN PK, so we have a consistent ordering, even if new items are created
			// PKs are random, so they don't increment!
			boolean orderByNeeded = false;

			// make additional query to fetch the pks, applied the "maxResults" correctly
			if (isPkQueryForPaginationNeeded) {
				// we always have to order in case of a PK subquery for both queries!
				orderByNeeded = true;

				CriteriaQuery<Long> pkCriteriaQuery = builder.createQuery(Long.class);
				final Root<T> pkRoot = pkCriteriaQuery.from(sourceQuery.getResultClass());
				pkCriteriaQuery = pkCriteriaQuery.select(pkRoot.get(Item.PROPERTY_PK));

				// apply original where clause here, it will be indirectly applied to the original query using the fetched PKs
				if (whereClause != null) {
					pkCriteriaQuery = pkCriteriaQuery.where(whereClause);
				}

				// always apply the same order for all queries
				final TypedQuery<Long> pkQuery = session.createQuery(pkCriteriaQuery.orderBy(applyOrderBy(sourceQuery, builder, pkRoot)));
				setPagination(pkQuery, sourceQuery.getPage(), sourceQuery.getPageSize());

				final List<Long> pksToSelect = pkQuery.getResultList();

				// only add where clause when there are actual PKs to select
				if (pksToSelect.size() > 0) {
					itemSelect = itemSelect.where(queryResultType.get(Item.PROPERTY_PK).in(pksToSelect));
				}
			} else {
				if (whereClause != null) {
					itemSelect = itemSelect.where(whereClause);
				}

				// if we have a single query, we only need to order if pagination is used
				if (sourceQuery.getOrderBy().size() > 0) {
					orderByNeeded = true;
				}
			}

			if (orderByNeeded) {
				// always apply the order here again, even if using pk sub-query!
				itemSelect = itemSelect.orderBy(applyOrderBy(sourceQuery, builder, queryResultType));
			}

			final TypedQuery<T> query = session.createQuery(itemSelect);

			// only set these values if no fetch joins are used!
			// if we have fetch joins we just select by the pks that are fetched before using firstResult and maxResults
			if (!isPkQueryForPaginationNeeded) {
				setPagination(query, sourceQuery.getPage(), sourceQuery.getPageSize());
			}

			setFetchSubGraphsHint(session, sourceQuery, query);
			setCacheSettings(session, sourceQuery, query);

			final List<T> results = ((Query<T>) query).getResultList();

			return results;
		});
	}

	/**
	 * Generates the ORDER BY clause either for the {@link ModelQuery#getOrderBy()} or if empty for the default properties ({@link Item#PROPERTY_CREATED_AT} and
	 * {@link Item#PROPERTY_PK}).
	 * 
	 * @param sourceQuery
	 * @param builder
	 * @param root
	 * @return the generated order by clause
	 */
	protected Order[] applyOrderBy(final ModelQuery<?> sourceQuery, CriteriaBuilder builder, Root<?> root) {
		final List<Order> orderBys = new ArrayList<>();

		if (sourceQuery.getOrderBy().size() > 0) {
			for (SortOrder order : sourceQuery.getOrderBy()) {
				if (OrderDirection.ASC.equals(order.getDirection())) {
					orderBys.add(builder.asc(root.get(order.getColumnName())));
				} else {
					orderBys.add(builder.desc(root.get(order.getColumnName())));
				}
			}
		} else {
			orderBys.add(builder.asc(root.get(Item.PROPERTY_CREATED_AT)));
			orderBys.add(builder.asc(root.get(Item.PROPERTY_PK)));
		}

		return orderBys.toArray(new Order[orderBys.size()]);
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> void remove(final List<T> items) {
		bindSession();

		transactionService.execute(() -> {
			for (final T item : items) {
				getSession().remove(item);
			}
			return null;
		});
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> void remove(final Class<T> type, final long pk) {
		bindSession();

		transactionService.execute(() -> {
			// TODO: improve
			// final String query = String.format("DELETE FROM %s WHERE pk IN
			// (?pk)", type.getSimpleName());

			// em.createQuery(query, type).setParameter("pk", pk);
			final T item = getSession().find(type, pk);
			getSession().remove(item);

			return null;
		});
	}

	/** {@inheritDoc} */
	@Override
	public void saveDataStorage() {
		bindSession();

		getSession().flush();
	}

	/** {@inheritDoc} */
	@Override
	public void clearDataStorage() {
		bindSession();

		// em.clear();
		getSession().clear();
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> void initItem(final T item) {
		for (final Field field : ClassUtil.getFieldsWithAnnotation(item.getClass(), Property.class)) {
			Object instanceValue = ClassUtil.getField(item, field.getName(), true);

			if (instanceValue == null) {
				if (field.getType().isAssignableFrom(Set.class)) {
					instanceValue = new HashSet<>();
				} else if (field.getType().isAssignableFrom(List.class)
						|| field.getType().isAssignableFrom(Collection.class)) {
					instanceValue = new ArrayList<>();
				} else if (field.getType().isAssignableFrom(Map.class)) {
					instanceValue = new HashMap<>();
				}

				if (instanceValue != null) {
					ClassUtil.setField(item, field.getName(), instanceValue);
				}
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> void detach(final List<T> items) {
		bindSession();

		for (final T item : items) {
			getSession().detach(item);
		}
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> boolean isAttached(final T item) {
		bindSession();

		return getSession().contains(item);
	}

	public Session getSession() {
		final EntityManagerHolder holder = ((EntityManagerHolder) TransactionSynchronizationManager
				.getResource(entityManagerFactory));

		if (holder != null) {
			if (Logger.isLogLevelEnabled(LogLevel.DEBUG)) {
				getSessionFactory().getStatistics().setStatisticsEnabled(true);
			}

			return holder.getEntityManager().unwrap(Session.class);
		}

		throw new IllegalStateException("Could not fetch persistence entity manager");
	}

	protected void bindSession() {
		if (!TransactionSynchronizationManager.hasResource(entityManagerFactory)) {
			TransactionSynchronizationManager.bindResource(entityManagerFactory,
					new EntityManagerHolder(entityManagerFactory.createEntityManager()));
		}
	}

	/** {@inheritDoc} */
	@Override
	public void unbindSession() {
		if (TransactionSynchronizationManager.hasResource(entityManagerFactory)) {
			final EntityManagerHolder emHolder = (EntityManagerHolder) TransactionSynchronizationManager
					.unbindResource(entityManagerFactory);
			EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
		}
	}

	/**
	 * <p>
	 * Getter for the field <code>entityManagerFactory</code>.
	 * </p>
	 *
	 * @return a {@link javax.persistence.EntityManagerFactory} object.
	 */
	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	/**
	 * <p>
	 * getSessionFactory.
	 * </p>
	 *
	 * @return a {@link SessionFactory} object.
	 */
	public SessionFactory getSessionFactory() {
		return entityManagerFactory.unwrap(SessionFactory.class);
	}

	public Statistics getStatistics() {
		return getSessionFactory().getStatistics();
	}

}

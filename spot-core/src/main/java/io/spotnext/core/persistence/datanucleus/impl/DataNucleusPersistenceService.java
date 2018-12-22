package io.spotnext.core.persistence.datanucleus.impl;

import java.util.Collections;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.metadata.PersistenceUnitMetaData;
import org.datanucleus.store.schema.CurrentUserProvider;
import org.springframework.beans.factory.annotation.Autowired;

import io.spotnext.core.infrastructure.exception.ModelNotFoundException;
import io.spotnext.core.infrastructure.exception.ModelSaveException;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.infrastructure.service.UserService;
import io.spotnext.core.persistence.exception.ModelNotUniqueException;
import io.spotnext.core.persistence.exception.QueryException;
import io.spotnext.core.persistence.query.JpqlQuery;
import io.spotnext.core.persistence.query.ModelQuery;
import io.spotnext.core.persistence.query.QueryResult;
import io.spotnext.core.persistence.service.impl.AbstractPersistenceService;
import io.spotnext.infrastructure.type.Item;
import io.spotnext.infrastructure.type.ItemTypeDefinition;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;

public class DataNucleusPersistenceService extends AbstractPersistenceService {

	private PersistenceUnitMetaData persistenceUnitMetaData;
	private PersistenceManagerFactory persistenceManagerFactory;

	private TypeService typeService;
	private UserService<User, UserGroup> userService;

	public DataNucleusPersistenceService(@Autowired TypeService typeService, @Autowired UserService<User, UserGroup> userService)
			throws ClassNotFoundException {
		this.typeService = typeService;
		this.userService = userService;

//		JDOEnhancer enhancer = JDOHelper.getEnhancer();
//		enhancer.setVerbose(true);
//		enhancer.addPersistenceUnit("spot");

		persistenceUnitMetaData = new PersistenceUnitMetaData("spot", "RESOURCE_LOCAL", null);

		for (ItemTypeDefinition def : typeService.getItemTypeDefinitions().values()) {
			Class<?> type = Class.forName(def.getTypeClass());
			persistenceUnitMetaData.addClassName(type.getName());
//			enhancer.addClasses(type.getName());
		}

		persistenceUnitMetaData.setExcludeUnlistedClasses(false);
		persistenceUnitMetaData.addProperty("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
		persistenceUnitMetaData.addProperty("javax.jdo.option.ConnectionURL", "jdbc:hsqldb:mem:nucleus");
		persistenceUnitMetaData.addProperty("javax.jdo.option.ConnectionUserName", "login");
		persistenceUnitMetaData.addProperty("javax.jdo.option.ConnectionPassword", "password");
		persistenceUnitMetaData.addProperty("datanucleus.schema.autoCreateAll", "true");
		persistenceUnitMetaData.addProperty("datanucleus.manageRelationships", "true");
		persistenceUnitMetaData.addProperty("datanucleus.manageRelationshipsChecks", "true");
		persistenceUnitMetaData.addProperty("datanucleus.persistenceByReachabilityAtCommit", "true");
		persistenceUnitMetaData.addProperty("datanucleus.CurrentUserProvider",
				"io.spotnext.core.persistence.datanucleus.impl.DataNucleusPersistenceService.UserProvider");

//		enhancer.enhance();

		persistenceManagerFactory = new JDOPersistenceManagerFactory(persistenceUnitMetaData, null);
	}

	protected PersistenceManager getSession() {
		return persistenceManagerFactory.getPersistenceManager();
	}

	@Override
	public <T extends Item> void save(List<T> items) throws ModelSaveException, ModelNotUniqueException {
		getSession().makePersistentAll(items);
	}

	@Override
	public <T extends Item> void refresh(List<T> items) throws ModelNotFoundException {
		getSession().refreshAll(items);
	}

	@Override
	public <T> QueryResult<T> query(JpqlQuery<T> query) throws QueryException {
		Query<T> q = getSession().newQuery(query.getQuery());
		q.setNamedParameters(query.getParams());
		q.setRange(query.getPage() * query.getPageSize(), (query.getPage() + 1) * query.getPageSize());
		List<T> results = q.executeList();

		return new QueryResult<T>(results, query.getPage(), query.getPageSize(), Long.valueOf(results.size()));
	}

	@Override
	public <T extends Item> T load(Class<T> type, long id, boolean returnProxy) throws ModelNotFoundException {
		return getSession().getObjectById(type, id);
	}

	@Override
	public <T extends Item> List<T> load(ModelQuery<T> query) {

		return null;
	}

	@Override
	public <T extends Item> void remove(List<T> items) {
		getSession().deletePersistentAll(items);
	}

	@Override
	public <T extends Item> void remove(Class<T> type, long id) {
		final Query<T> deleteQuery = getSession().newQuery("DELETE FROM " + type.getSimpleName() + " WHERE id = :id");
		deleteQuery.setNamedParameters(Collections.singletonMap("id", id));
		List<?> result = deleteQuery.executeList();

		if (result.size() != 1) {
			throw new IllegalStateException(String.format("Could not delete instance of type %s with id=%s", type.getSimpleName(), id));
		}
	}

	@Override
	public void saveDataStorage() {
		getSession().flush();
	}

	@Override
	public void clearDataStorage() {
		//
	}

	@Override
	public <T extends Item> void initItem(T item) {
		// TODO Auto-generated method stub
	}

	@Override
	public <T extends Item> void detach(List<T> items) {
		getSession().detachCopyAll(items);
	}

	@Override
	public <T extends Item> boolean isAttached(T item) {
		return true;
	}

	@Override
	public <T extends Item> boolean attach(T item) throws ModelNotFoundException {
		return true;
	}

	@Override
	public void unbindSession() {
		//
	}

	@Override
	public void evictCaches() {
		getSession().evictAll();
	}

	@Override
	public void destroy() throws Exception {
		persistenceManagerFactory.close();
	}

	public class UserProvider implements CurrentUserProvider {
		@Override
		public String currentUser() {
			return userService.getCurrentUser().getUid();
		}
	}

}

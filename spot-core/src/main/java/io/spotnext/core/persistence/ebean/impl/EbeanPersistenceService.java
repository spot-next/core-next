package io.spotnext.core.persistence.ebean.impl;

import java.util.List;
import java.util.Map;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.spotnext.core.infrastructure.exception.ModelNotFoundException;
import io.spotnext.core.infrastructure.exception.ModelSaveException;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.infrastructure.support.spring.PostConstructor;
import io.spotnext.core.persistence.exception.ModelNotUniqueException;
import io.spotnext.core.persistence.exception.QueryException;
import io.spotnext.core.persistence.query.JpqlQuery;
import io.spotnext.core.persistence.query.ModelQuery;
import io.spotnext.core.persistence.query.QueryResult;
import io.spotnext.core.persistence.service.PersistenceService;
import io.spotnext.infrastructure.type.Item;

public class EbeanPersistenceService extends AbstractService implements PersistenceService, PostConstructor {

	private EbeanServer ebeanService = Ebean.getDefaultServer();

	@Override
	public void setup() {
	}

	@Override
	public <T extends Item> void save(List<T> models) throws ModelSaveException, ModelNotUniqueException {
		ebeanService.saveAll(models);
	}

	@Override
	public <T extends Item> void refresh(List<T> item) throws ModelNotFoundException {
		ebeanService.refresh(item);
	}

	@Override
	public <T> QueryResult<T> query(JpqlQuery<T> query) throws QueryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Item> T load(Class<T> type, long id, boolean returnProxy) throws ModelNotFoundException {
		return ebeanService.find(type, id);
	}

	@Override
	public <T extends Item> List<T> load(ModelQuery<T> query) {
//		ebeanService.
		
		return null;
	}

	@Override
	public <T extends Item> void remove(List<T> items) {
		ebeanService.deleteAll(items);
	}

	@Override
	public <T extends Item> void remove(Class<T> type, long id) {
		ebeanService.delete(type, id);
	}

	@Override
	public void saveDataStorage() {
		ebeanService.flush();
		//
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
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends Item> Map<String, Object> convertItemToMap(T item) {
		// TODO Auto-generated method stub
		return null;
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
		//
	}
}

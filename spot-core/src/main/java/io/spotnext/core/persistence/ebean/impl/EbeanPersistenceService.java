package io.spotnext.core.persistence.ebean.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.ebean.EbeanServer;
import io.ebean.EbeanServerFactory;
import io.ebean.config.ServerConfig;
import io.spotnext.core.infrastructure.exception.ModelNotFoundException;
import io.spotnext.core.infrastructure.exception.ModelSaveException;
import io.spotnext.core.infrastructure.exception.UnknownTypeException;
import io.spotnext.core.infrastructure.service.ConfigurationService;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.core.persistence.exception.ModelNotUniqueException;
import io.spotnext.core.persistence.exception.QueryException;
import io.spotnext.core.persistence.query.JpqlQuery;
import io.spotnext.core.persistence.query.ModelQuery;
import io.spotnext.core.persistence.query.QueryResult;
import io.spotnext.core.persistence.service.PersistenceService;
import io.spotnext.core.persistence.service.TransactionService;
import io.spotnext.infrastructure.type.Item;
import io.spotnext.infrastructure.type.ItemTypeDefinition;
import io.spotnext.itemtype.core.UniqueIdItem;

@Service
public class EbeanPersistenceService extends AbstractService implements PersistenceService {

	private final EbeanServer ebeanService;
	private final ConfigurationService configurationService;
	private final TransactionService transactionService;
	private final TypeService typeService;

	public EbeanPersistenceService(TransactionService transactionService, ConfigurationService configurationService, TypeService typeService)
			throws UnknownTypeException {

		Logger.info("Initializing persistence service");

		this.transactionService = transactionService;
		this.configurationService = configurationService;
		this.typeService = typeService;

		ServerConfig config = new ServerConfig();
		config.loadFromProperties();
		config.setName("spot");
		config.setDefaultServer(true);
		config.getClasses().add(Item.class);
		config.getClasses().add(UniqueIdItem.class);

		for (ItemTypeDefinition typeDef : typeService.getItemTypeDefinitions().values()) {
			Class<? extends Item> itemType = typeService.getClassForTypeCode(typeDef.getTypeCode());
			config.getClasses().add(itemType);
		}

		this.ebeanService = EbeanServerFactory.create(config);
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

package at.spot.core.persistence.service.impl.zoodb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.zoodb.jdo.ZooJdoHelper;

import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.CannotCreateModelProxyException;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.persistence.service.impl.AbstractPersistenceService;

public class ZooDbPersistenceService extends AbstractPersistenceService implements PersistenceService {

	protected static final String DB_FILE = "/var/tmp/spot.zdb";

	protected PersistenceManager pm;

	public ZooDbPersistenceService() {
		this.pm = ZooJdoHelper.openOrCreateDB(DB_FILE);
	}

	@Override
	public <T extends Item> void save(T... models) throws ModelSaveException, ModelNotUniqueException {
		save(Arrays.asList(models));
	}

	@Override
	public <T extends Item> void save(List<T> models) throws ModelSaveException, ModelNotUniqueException {
		try {
			beginTransaction();
			pm.makePersistentAll(models);
			commit();
		} catch (Exception e) {
			rollback();
		}
	}

	@Override
	public <T extends Item> T load(Class<T> type, long pk) throws ModelNotFoundException {
		Query q = pm.newQuery(type, "pk == '" + pk + "'");
		Collection<T> models = (Collection<T>) q.execute();

		if (CollectionUtils.isNotEmpty(models)) {
			return models.iterator().next();
		}

		throw new ModelNotFoundException(type, pk);
	}

	@Override
	public <T extends Item> void refresh(T item) throws ModelNotFoundException {
		pm.refresh(item);
	}

	@Override
	public <T extends Item> List<T> load(Class<T> type, Map<String, Object> searchParameters) {
		return load(type, searchParameters, null, null);
	}

	@Override
	public <T extends Item> List<T> load(Class<T> type, Map<String, Object> searchParameters, Integer page,
			Integer pageSize) {

		List<String> criteria = buildQueryStringWithNonItemTypeProperties(searchParameters, null);
		String criteriaString = StringUtils.join(criteria, " AND ");

		Query query = pm.newQuery(type, criteriaString);

		return new ArrayList<T>((Collection<T>) query.execute());
	}

	protected <T extends Item> List<String> buildQueryStringWithNonItemTypeProperties(Map<String, Object> params,
			String propertyName) {

		List<String> criteria = new ArrayList<>();

		String prefix = propertyName != null ? propertyName + "." : "";

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			if (!(entry.getValue() instanceof Item)) {
				criteria.add(String.format("%s%s == '%s'", prefix, entry.getKey(), entry.getValue()));
			} else {
				Map<String, Object> subItem = convertItemToMap((Item) entry.getValue());
				criteria.addAll(buildQueryStringWithNonItemTypeProperties(subItem, entry.getKey()));
			}
		}

		return criteria;
	}

	@Override
	public <T extends Item> void loadProxyModel(T proxyItem) throws ModelNotFoundException {
		refresh(proxyItem);
	}

	@Override
	public <T extends Item> T createProxyModel(T item) throws CannotCreateModelProxyException {
		return item;
	}

	@Override
	public <T extends Item> void remove(T... items) {
		pm.deletePersistentAll(Arrays.asList(items));
	}

	@Override
	public <T extends Item> void remove(Class<T> type, long pk) {
		T item;
		try {
			item = load(type, pk);
			pm.deletePersistent(item);
		} catch (ModelNotFoundException e) {
			loggingService
					.debug(String.format("Item of type %s with pk %s not found for deletion.", type.getName(), pk));
		}
	}

	@Override
	public void saveDataStorage() {
		pm.flush();
	}

	@Override
	public void clearDataStorage() {
	}

	@Override
	public <T extends Item> void initItem(T item) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends Item> void detach(T... items) {
		pm.detachCopyAll(items);
	}

	protected void beginTransaction() {
		this.pm.currentTransaction().begin();
	}

	protected void commit() {
		this.pm.currentTransaction().commit();
	}

	protected void rollback() {
		this.pm.currentTransaction().rollback();
	}

}

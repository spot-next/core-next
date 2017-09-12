package at.spot.core.infrastructure.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.model.Item;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.support.util.ClassUtil;

@SuppressWarnings("unchecked")
@Service
public abstract class AbstractModelService extends AbstractService implements ModelService {

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected PersistenceService persistenceService;

	@Override
	public <T extends Item> T create(final Class<T> type) {
		final String typeCode = typeService.getTypeCode(type);

		final T item = getApplicationContext().getBean(typeCode, type);
		setTypeCode(item);
		persistenceService.initItem(item);

		return item;
	}

	protected <T extends Item> void setTypeCode(final T item) {
		ClassUtil.setField(item, "typeCode", typeService.getTypeCode(item.getClass()));
	}

	@Override
	public <T extends Item> T createProxyModel(final Class<T> itemType, final long pk) {
		T proxyItem = null;

		proxyItem = create(itemType);
		proxyItem.setPk(pk);
		// ClassUtil.setField(proxyItem, "isProxy", true);
		proxyItem.setIsProxy(true);

		return proxyItem;
	}

	@Override
	public <T extends Item> T createProxyModel(final T item) {
		return createProxyModel((Class<T>) item.getClass(), item.getPk());
	}

	@Override
	public <T extends Item> void detach(final T... items) {
		persistenceService.detach(items);
	}

	@Override
	public <T extends Item> void detach(final List<T> items) {
		detach(items.toArray(new Item[0]));
	}
}

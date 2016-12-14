package at.spot.core.infrastructure.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.model.Item;
import at.spot.core.support.util.ClassUtil;

@SuppressWarnings("unchecked")
@Service
public abstract class AbstractModelService extends AbstractService implements ModelService {

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected LoggingService loggingService;

	@Override
	public <T extends Item> T create(final Class<T> type) {
		final T item = getApplicationContext().getBean(type);
		setTypeCode(item);
		return item;
	}

	protected <T extends Item> void setTypeCode(final T item) {
		ClassUtil.setField(item, "typeCode", typeService.getTypeCode(item.getClass()));
	}

	@Override
	public <T extends Item> T createProxyModel(final Class<T> itemType, final long pk) {

		T proxyItem = null;

		proxyItem = create(itemType);
		ClassUtil.setField(proxyItem, "isProxy", true);

		proxyItem.pk = pk;

		return proxyItem;
	}

	@Override
	public <T extends Item> T createProxyModel(final T item) {
		return createProxyModel((Class<T>) item.getClass(), item.pk);
	}
}

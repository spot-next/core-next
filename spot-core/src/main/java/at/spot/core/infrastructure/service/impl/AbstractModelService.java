package at.spot.core.infrastructure.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.interceptor.OnItemCreateListener;
import at.spot.core.infrastructure.interceptor.OnItemLoadListener;
import at.spot.core.infrastructure.interceptor.OnItemSaveListener;
import at.spot.core.infrastructure.interceptor.OnItemValidateListener;
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

	protected Map<Class<? extends Item>, OnItemSaveListener> saveListeners;
	protected Map<Class<? extends Item>, OnItemValidateListener> validateListeners;
	protected Map<Class<? extends Item>, OnItemLoadListener> loadListeners;
	protected Map<Class<? extends Item>, OnItemCreateListener> createListeners;

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
	public <T extends Item> void detach(final T... items) {
		persistenceService.detach(items);
	}

	@Override
	public <T extends Item> void detach(final List<T> items) {
		detach(items.toArray(new Item[0]));
	}

	@Required
	public void setSaveListeners(final Map<Class<? extends Item>, OnItemSaveListener> saveListeners) {
		this.saveListeners = saveListeners;
	}

	@Required
	public void setValidateListeners(final Map<Class<? extends Item>, OnItemValidateListener> validateListeners) {
		this.validateListeners = validateListeners;
	}

	@Required
	public void setLoadListeners(final Map<Class<? extends Item>, OnItemLoadListener> loadListeners) {
		this.loadListeners = loadListeners;
	}

	@Required
	public void setCreateListeners(final Map<Class<? extends Item>, OnItemCreateListener> createListeners) {
		this.createListeners = createListeners;
	}

}

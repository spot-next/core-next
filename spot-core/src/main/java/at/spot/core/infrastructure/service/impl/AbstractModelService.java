package at.spot.core.infrastructure.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.exception.ModelValidationException;
import at.spot.core.infrastructure.interceptor.ItemModificationListener;
import at.spot.core.infrastructure.interceptor.OnItemCreateListener;
import at.spot.core.infrastructure.interceptor.OnItemLoadListener;
import at.spot.core.infrastructure.interceptor.OnItemSaveListener;
import at.spot.core.infrastructure.interceptor.OnItemValidateListener;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.support.util.ClassUtil;

@SuppressWarnings("unchecked")
@Service
public abstract class AbstractModelService extends AbstractService implements ModelService {

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected PersistenceService persistenceService;

	@Autowired(required = false)
	protected List<OnItemSaveListener> saveListeners = Collections.emptyList();
	@Autowired(required = false)
	protected List<OnItemValidateListener> validateListeners = Collections.emptyList();
	@Autowired(required = false)
	protected List<OnItemLoadListener> loadListeners = Collections.emptyList();
	@Autowired(required = false)
	protected List<OnItemCreateListener> createListeners = Collections.emptyList();

	final protected Map<Class<Item>, List<ItemModificationListener<Item>>> createListenerRegistry = new HashMap<>();
	final protected Map<Class<Item>, List<ItemModificationListener<Item>>> validateListenerRegistry = new HashMap<>();
	final protected Map<Class<Item>, List<ItemModificationListener<Item>>> saveListenerRegistry = new HashMap<>();
	final protected Map<Class<Item>, List<ItemModificationListener<Item>>> loadListenerRegistry = new HashMap<>();

	@PostConstruct
	protected void setup() {
		createListeners.stream().forEach(l -> addToListeners(l.getItemType(), l, createListenerRegistry));
		validateListeners.stream().forEach(l -> addToListeners(l.getItemType(), l, validateListenerRegistry));
		saveListeners.stream().forEach(l -> addToListeners(l.getItemType(), l, saveListenerRegistry));
		loadListeners.stream().forEach(l -> addToListeners(l.getItemType(), l, loadListenerRegistry));
	}

	protected void addToListeners(final Class<Item> itemType, final ItemModificationListener<Item> listener,
			final Map<Class<Item>, List<ItemModificationListener<Item>>> map) {

		// register all listeners for all superclasses of type Item
		for (final Class<?> superClass : ClassUtil.getAllAssignableClasses(itemType)) {
			if (superClass.isAssignableFrom(Item.class)) {
				List<ItemModificationListener<Item>> listeners = map.get(superClass);

				if (listeners == null) {
					listeners = new ArrayList<>();
					map.put((Class<Item>) superClass, listeners);
				}

				listeners.add(listener);
			}
		}
	}

	@Override
	public <T extends Item> T create(final Class<T> type) {
		final String typeCode = typeService.getTypeCode(type);

		final T item = getApplicationContext().getBean(typeCode, type);
		setTypeCode(item);
		persistenceService.initItem(item);

		List<ItemModificationListener<Item>> listeners = createListenerRegistry.get(item.getClass());

		if (listeners != null) {
			listeners.stream().forEach(l -> l.onEvent(item));
		}

		return item;
	}

	@Override
	public <T extends Item> void saveAll(List<T> models)
			throws ModelSaveException, ModelNotUniqueException, ModelValidationException {

		for (T item : models) {
			List<ItemModificationListener<Item>> listeners = saveListenerRegistry.get(item.getClass());

			if (listeners != null) {
				listeners.stream().forEach(l -> l.onEvent(item));
			}
		}
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
}

package at.spot.core.infrastructure.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.exception.ModelValidationException;
import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.interceptor.ItemCreateInterceptor;
import at.spot.core.infrastructure.interceptor.ItemInterceptor;
import at.spot.core.infrastructure.interceptor.ItemLoadInterceptor;
import at.spot.core.infrastructure.interceptor.ItemSaveInterceptor;
import at.spot.core.infrastructure.interceptor.ItemValidateInterceptor;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.exception.SerialNumberGeneratorException;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.persistence.service.SerialNumberGeneratorService;
import at.spot.core.support.util.ClassUtil;
import at.spot.itemtype.core.UniqueIdItem;

@SuppressWarnings("unchecked")
@Service
public abstract class AbstractModelService extends AbstractService implements ModelService {

	@Resource
	protected SerialNumberGeneratorService serialNumberGeneratorService;

	@Resource
	protected TypeService typeService;

	@Resource
	protected PersistenceService persistenceService;

	@Autowired(required = false)
	protected List<ItemCreateInterceptor> createInterceptors = Collections.emptyList();
	@Autowired(required = false)
	protected List<ItemValidateInterceptor> validateInterceptors = Collections.emptyList();
	@Autowired(required = false)
	protected List<ItemSaveInterceptor> saveInterceptors = Collections.emptyList();
	@Autowired(required = false)
	protected List<ItemLoadInterceptor> loadInterceptors = Collections.emptyList();

	final protected Map<String, List<ItemCreateInterceptor<Item>>> createInterceptorRegistry = new HashMap<>();
	final protected Map<String, List<ItemValidateInterceptor<Item>>> validateInterceptorRegistry = new HashMap<>();
	final protected Map<String, List<ItemSaveInterceptor<Item>>> saveInterceptorRegistry = new HashMap<>();
	final protected Map<String, List<ItemLoadInterceptor<Item>>> loadInterceptorRegistry = new HashMap<>();

	@PostConstruct
	protected void setup() throws UnknownTypeException {
		// for (final ItemTypeDefinition def :
		// typeService.getItemTypeDefinitions().values()) {
		// final Class<? extends Item> itemType =
		// typeService.getType(def.getTypeCode());
		//
		// List<ItemModificationListener<Item>> interceptors =
		// createInterceptorRegistry.get(def.getTypeCode());
		//
		// if (interceptors == null) {
		// interceptors = new ArrayList<>();
		// createInterceptorRegistry.put(def.getTypeCode(), interceptors);
		// }
		//
		// for (final OnItemCreateListener i : createInterceptors) {
		// final String currentTypeCode =
		// typeService.getTypeCode(i.getItemType());
		// }
		// }

		// createInterceptors.stream().forEach(l ->
		// addToListeners(l.getItemType(), l, createInterceptorRegistry));
		// validateInterceptors.stream().forEach(l ->
		// addToListeners(l.getItemType(), l, validateInterceptorRegistry));
		// saveInterceptors.stream().forEach(l ->
		// addToListeners(l.getItemType(), l, saveInterceptorRegistry));
		// loadInterceptors.stream().forEach(l ->
		// addToListeners(l.getItemType(), l, loadInterceptorRegistry));
	}

	protected void addToListeners(final Class<Item> itemType, final ItemInterceptor<Item> listener,
			final Map<Class<Item>, List<ItemInterceptor<Item>>> map) {

		// register all listeners for all superclasses of type Item
		List<ItemInterceptor<Item>> listeners = map.get(itemType);

		if (listeners == null) {
			listeners = new ArrayList<>();
			map.put(itemType, listeners);
		}

		listeners.add(listener);
	}

	@Override
	public <T extends Item> T create(final Class<T> type) {
		final String typeCode = typeService.getTypeCodeForClass(type);

		final T item = getApplicationContext().getBean(typeCode, type);
		setTypeCode(item);
		persistenceService.initItem(item);

		final List<ItemCreateInterceptor<Item>> listeners = createInterceptorRegistry.get(item.getClass());

		if (listeners != null) {
			listeners.stream().forEach(l -> l.onCreate(item));
		}

		return item;
	}

	@Override
	public <T extends Item> void saveAll(final List<T> models)
			throws ModelSaveException, ModelNotUniqueException, ModelValidationException {

		for (final T item : models) {
			if (item instanceof UniqueIdItem) {
				try {
					serialNumberGeneratorService.generate((UniqueIdItem) item);
				} catch (final SerialNumberGeneratorException e) {
					throw new ModelSaveException(String.format("Could not generate serial number for item of type %s",
							item.getClass().getName()), e);
				}
			}

			final List<ItemSaveInterceptor<Item>> listeners = saveInterceptorRegistry.get(item.getClass());

			if (listeners != null) {
				listeners.stream().forEach(l -> l.onSave(item));
			}
		}
	}

	protected <T extends Item> void setTypeCode(final T item) {
		ClassUtil.setField(item, "typeCode", typeService.getTypeCodeForClass(item.getClass()));
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

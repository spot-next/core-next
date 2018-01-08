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
import at.spot.core.infrastructure.interceptor.ItemCreateInterceptor;
import at.spot.core.infrastructure.interceptor.ItemInterceptor;
import at.spot.core.infrastructure.interceptor.ItemLoadInterceptor;
import at.spot.core.infrastructure.interceptor.ItemSaveInterceptor;
import at.spot.core.infrastructure.interceptor.ItemValidateInterceptor;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.support.ItemTypeDefinition;
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
	protected List<ItemSaveInterceptor<Item>> saveInterceptors = Collections.emptyList();
	@Autowired(required = false)
	protected List<ItemValidateInterceptor<Item>> validateInterceptors = Collections.emptyList();
	@Autowired(required = false)
	protected List<ItemLoadInterceptor<Item>> loadInterceptors = Collections.emptyList();
	@Autowired(required = false)
	protected List<ItemCreateInterceptor<Item>> createInterceptors = Collections.emptyList();

	final protected Map<Class<Item>, List<ItemLoadInterceptor<Item>>> loadInterceptorRegistry = new HashMap<>();
	final protected Map<Class<Item>, List<ItemCreateInterceptor<Item>>> createInterceptorRegistry = new HashMap<>();
	final protected Map<Class<Item>, List<ItemValidateInterceptor<Item>>> validateInterceptorRegistry = new HashMap<>();
	final protected Map<Class<Item>, List<ItemSaveInterceptor<Item>>> saveInterceptorRegistry = new HashMap<>();

	@PostConstruct
	protected void setup() {
		for (Map.Entry<String, ItemTypeDefinition> entry : typeService.getItemTypeDefinitions().entrySet()) {

		}

		createInterceptors.stream().forEach(l -> addToInterceptors(l.getItemType(), l, createInterceptorRegistry));
		validateInterceptors.stream().forEach(l -> addToInterceptors(l.getItemType(), l, validateInterceptorRegistry));
		saveInterceptors.stream().forEach(l -> addToInterceptors(l.getItemType(), l, saveInterceptorRegistry));
		loadInterceptors.stream().forEach(l -> addToInterceptors(l.getItemType(), l, loadInterceptorRegistry));

		// loadInterceptors.stream().collect(Collectors.toMap(ItemLoadInterceptor::getItemType,
		// Function.identity()));
		// createInterceptors.stream().collect(Collectors.toMap(ItemCreateInterceptor::getItemType,
		// Function.identity()));
		// validateInterceptors.stream()
		// .collect(Collectors.toMap(ItemValidateInterceptor::getItemType,
		// Function.identity()));
		// saveInterceptors.stream().collect(Collectors.toMap(ItemSaveInterceptor::getItemType,
		// Function.identity()));
	}

	protected <T extends ItemInterceptor<Item>> void addToInterceptors(final Class<Item> itemType, final T interceptor,
			final Map<Class<Item>, List<T>> map) {

		List<T> interceptors = map.get(itemType);

		if (interceptors == null) {
			interceptors = new ArrayList<>();
			map.put((Class<Item>) itemType, interceptors);
		}

		interceptors.add(interceptor);
	}

	@Override
	public <T extends Item> T create(final Class<T> itemType) {
		final String typeCode = typeService.getTypeCodeForClass(itemType);

		final T item = getApplicationContext().getBean(typeCode, itemType);
		setTypeCode(item);
		persistenceService.initItem(item);

		List<ItemCreateInterceptor<Item>> interceptors = createInterceptorRegistry.get(item.getClass());

		// for (Class<?> superClass : ClassUtil.getAllAssignableClasses(itemType)) {
		// if (superClass.isAssignableFrom(Item.class)) {
		// List<ItemCreateInterceptor<Item>> typeInterceptors =
		//
		// if (typeInterceptors != null) {
		// interceptors.addAll(typeInterceptors);
		// }
		// }
		// }

		if (interceptors != null) {
			interceptors.stream().forEach(l -> l.onCreate(item));
		}

		return item;
	}

	@Override
	public <T extends Item> void saveAll(List<T> models)
			throws ModelSaveException, ModelNotUniqueException, ModelValidationException {

		for (T item : models) {
			List<ItemSaveInterceptor<Item>> listeners = saveInterceptorRegistry.get(item.getClass());

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

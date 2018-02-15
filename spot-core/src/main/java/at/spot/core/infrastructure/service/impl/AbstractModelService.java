package at.spot.core.infrastructure.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.exception.ModelValidationException;
import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.interceptor.ItemCreateInterceptor;
import at.spot.core.infrastructure.interceptor.ItemLoadInterceptor;
import at.spot.core.infrastructure.interceptor.ItemSaveInterceptor;
import at.spot.core.infrastructure.interceptor.ItemValidateInterceptor;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.support.util.ClassUtil;

@SuppressWarnings("unchecked")
@Service
public abstract class AbstractModelService extends AbstractService implements ModelService {

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

	protected Map<Class, List<ItemCreateInterceptor>> createInterceptorRegistry;
	protected Map<Class, List<ItemValidateInterceptor>> validateInterceptorRegistry;
	protected Map<Class, List<ItemSaveInterceptor>> saveInterceptorRegistry;
	protected Map<Class, List<ItemLoadInterceptor>> loadInterceptorRegistry;

	@PostConstruct
	protected void setup() throws UnknownTypeException {
		// TODO: super type handling not yet implemented
		createInterceptorRegistry = createInterceptors.stream()
				.collect(Collectors.groupingBy(ItemCreateInterceptor::getItemType));
		validateInterceptorRegistry = validateInterceptors.stream()
				.collect(Collectors.groupingBy(ItemValidateInterceptor::getItemType));
		saveInterceptorRegistry = saveInterceptors.stream()
				.collect(Collectors.groupingBy(ItemSaveInterceptor::getItemType));
		loadInterceptorRegistry = loadInterceptors.stream()
				.collect(Collectors.groupingBy(ItemLoadInterceptor::getItemType));
	}

	@Override
	public <T extends Item> T create(final Class<T> type) {
		final String typeCode = typeService.getTypeCodeForClass(type);

		final T item = getApplicationContext().getBean(typeCode, type);
		setTypeCode(item);
		persistenceService.initItem(item);

		final List<ItemCreateInterceptor> listeners = createInterceptorRegistry.get(item.getClass());

		if (listeners != null) {
			listeners.stream().forEach(l -> l.onCreate(item));
		}

		return item;
	}

	@Override
	public <T extends Item> void saveAll(final List<T> models)
			throws ModelSaveException, ModelNotUniqueException, ModelValidationException {

		for (final T item : models) {
			final List<ItemSaveInterceptor> listeners = saveInterceptorRegistry.get(item.getClass());

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

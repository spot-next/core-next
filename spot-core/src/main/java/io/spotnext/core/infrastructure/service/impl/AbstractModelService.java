package io.spotnext.core.infrastructure.service.impl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.ConstraintViolation;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.annotation.logging.Log;
import io.spotnext.core.infrastructure.event.ItemModificationEvent;
import io.spotnext.core.infrastructure.event.ItemModificationEvent.ModificationType;
import io.spotnext.core.infrastructure.exception.ItemInterceptorException;
import io.spotnext.core.infrastructure.exception.ModelCreationException;
import io.spotnext.core.infrastructure.exception.ModelNotFoundException;
import io.spotnext.core.infrastructure.exception.ModelSaveException;
import io.spotnext.core.infrastructure.exception.ModelValidationException;
import io.spotnext.core.infrastructure.interceptor.ItemCreateInterceptor;
import io.spotnext.core.infrastructure.interceptor.ItemLoadInterceptor;
import io.spotnext.core.infrastructure.interceptor.ItemPrepareInterceptor;
import io.spotnext.core.infrastructure.interceptor.ItemRemoveInterceptor;
import io.spotnext.core.infrastructure.interceptor.ItemValidateInterceptor;
import io.spotnext.core.infrastructure.service.EventService;
import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.infrastructure.service.UserService;
import io.spotnext.core.infrastructure.service.ValidationService;
import io.spotnext.core.infrastructure.support.ItemInterceptorRegistry;
import io.spotnext.core.infrastructure.support.LogLevel;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.core.persistence.exception.ModelNotUniqueException;
import io.spotnext.core.persistence.service.PersistenceService;
import io.spotnext.infrastructure.type.Item;
import io.spotnext.infrastructure.type.Localizable;
import io.spotnext.itemtype.core.beans.UserData;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;
import io.spotnext.support.util.ClassUtil;

/**
 * <p>
 * Abstract AbstractModelService class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings("unchecked")
@Service
public abstract class AbstractModelService extends AbstractService implements ModelService {

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected UserService<User, UserGroup> userService;

	@Autowired
	protected EventService eventService;

	@Autowired
	protected PersistenceService persistenceService;

	@Autowired
	protected ValidationService validationService;

	@Autowired
	protected ItemInterceptorRegistry<ItemCreateInterceptor<Item>> itemCreateInterceptorRegistry;

	@Autowired
	protected ItemInterceptorRegistry<ItemValidateInterceptor<Item>> itemValidateInterceptorRegistry;

	@Autowired
	protected ItemInterceptorRegistry<ItemPrepareInterceptor<Item>> itemPrepareInterceptorRegistry;

	@Autowired
	protected ItemInterceptorRegistry<ItemLoadInterceptor<Item>> itemLoadInterceptorRegistry;

	@Autowired
	protected ItemInterceptorRegistry<ItemRemoveInterceptor<Item>> itemRemoveInterceptorRegistry;

	/** {@inheritDoc} */
	@Override
	public <T extends Item> T create(final Class<T> type) throws ModelCreationException {
		final String typeCode = typeService.getTypeCodeForClass(type);

		final T item = getApplicationContext().getBean(typeCode, type);
		persistenceService.initItem(item);

		for (final Class<?> superClass : ClassUtil.getAllSuperClasses(item.getClass(), Item.class, false, true)) {
			final String superTypeCode = typeService.getTypeCodeForClass((Class<Item>) superClass);
			final List<ItemCreateInterceptor<Item>> interceptors = itemCreateInterceptorRegistry
					.getValues(superTypeCode);

			if (CollectionUtils.isNotEmpty(interceptors)) {
				interceptors.stream().forEach(l -> l.onCreate(item));
			}
		}

		return item;
	}

	@Log(logLevel = LogLevel.DEBUG, message = "Executing load interceptors", measureExecutionTime = true)
	protected <T extends Item> void applyLoadInterceptors(final List<T> items) throws ItemInterceptorException {
		for (final T item : items) {
			for (final Class<?> superClass : ClassUtil.getAllSuperClasses(item.getClass(), Item.class, false, true)) {
				final String superTypeCode = typeService.getTypeCodeForClass((Class<Item>) superClass);
				final List<ItemLoadInterceptor<Item>> interceptors = itemLoadInterceptorRegistry
						.getValues(superTypeCode);

				if (CollectionUtils.isNotEmpty(interceptors)) {
					interceptors.stream().forEach(l -> l.onLoad(item));
				}
			}
		}
	}

	@Log(logLevel = LogLevel.DEBUG, message = "Executing remove interceptors", measureExecutionTime = true)
	protected <T extends Item> void applyRemoveInterceptors(final List<T> items) throws ItemInterceptorException {
		for (final T item : items) {
			for (final Class<?> superClass : ClassUtil.getAllSuperClasses(item.getClass(), Item.class, false, true)) {
				final String superTypeCode = typeService.getTypeCodeForClass((Class<Item>) superClass);
				final List<ItemRemoveInterceptor<Item>> interceptors = itemRemoveInterceptorRegistry
						.getValues(superTypeCode);

				if (CollectionUtils.isNotEmpty(interceptors)) {
					interceptors.stream().forEach(l -> l.onRemove(item));
				}
			}
		}
	}

	@Log(logLevel = LogLevel.DEBUG, message = "Executing prepare interceptors", measureExecutionTime = true)
	protected <T extends Item> void applyPrepareInterceptors(final List<T> items)
			throws ModelSaveException, ModelNotUniqueException, ModelValidationException {

		for (final T item : items) {
			for (final Class<?> superClass : ClassUtil.getAllSuperClasses(item.getClass(), Item.class, false, true)) {
				final String superTypeCode = typeService.getTypeCodeForClass((Class<Item>) superClass);
				final List<ItemPrepareInterceptor<Item>> interceptors = itemPrepareInterceptorRegistry
						.getValues(superTypeCode);

				if (CollectionUtils.isNotEmpty(interceptors)) {
					try {
						interceptors.stream().forEach(l -> l.onPrepare(item));
					} catch (final ItemInterceptorException e) {
						throw new ModelSaveException("Error while applying prepare interceptors.", e);
					}
				}
			}
		}
	}

	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	protected <T extends Item> void validateModels(final List<T> items) throws ModelValidationException {
		for (final T item : items) {
			validateModel(item);
		}
	}

	protected <T extends Item> void validateModel(final T item) throws ModelValidationException {
		if (item == null) {
			throw new ModelValidationException("Given item is null");
		}

		final Set<ConstraintViolation<T>> errors = validationService.validate(item);

		for (final Class<?> superClass : ClassUtil.getAllSuperClasses(item.getClass(), Item.class, false, true)) {
			final String superTypeCode = typeService.getTypeCodeForClass((Class<Item>) superClass);
			final List<ItemValidateInterceptor<Item>> interceptors = itemValidateInterceptorRegistry
					.getValues(superTypeCode);

			if (CollectionUtils.isNotEmpty(interceptors)) {
				try {
					interceptors.stream().forEach(l -> l.onValidate(item));
				} catch (ModelValidationException e) {
//					errors.addAll((Collection<? extends ConstraintViolation<T>>) e.getConstraintViolations());
					throw e;
				}
			}
		}

		if (!errors.isEmpty()) {
			final String message = validationService.convertToReadableMessage(Collections.unmodifiableSet(errors));

			throw new ModelValidationException(message, errors);
		}

		// travers all properties recursively and check field values of type Item
		ClassUtil.visitFields(item, (field) -> Item.class.isAssignableFrom(field.getType()),
				(field, subItem) -> validateModel((Item) subItem), true);
	}

	protected <T extends Item> void setTypeCode(final T item) {
		ClassUtil.setField(item, "typeCode", typeService.getTypeCodeForClass(item.getClass()));
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> void detach(final T... items) {
		persistenceService.detach(Arrays.asList(items));
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> void detach(final List<T> items) {
		detach(items.toArray(new Item[0]));
	}

	/**
	 * Sets the {@link io.spotnext.infrastructure.type.Item#setCreatedBy(String)} and {@link io.spotnext.infrastructure.type.Item#setLastModifiedBy(String)}.
	 *
	 * @param models a {@link java.util.List} object.
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	public <T extends Item> void setUserInformation(final List<T> models) {
		final UserData currentUser = userService.getCurrentUser();

		for (final T model : models) {
			ClassUtil.setField(model, "createdBy", currentUser.getUid());
			ClassUtil.setField(model, "lastModifiedBy", currentUser.getUid());
		}
	}

	/**
	 * Asynchronously publishes item modification events.
	 * 
	 * @param items            the items that have been modified
	 * @param modificationType the kind of modification that has been applied
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	protected <T extends Item> void publishEvents(final List<T> items, final ModificationType modificationType) {
		for (final T item : items) {
			if (item != null) {
				try {
					eventService.multicastEvent(new ItemModificationEvent<>(item, modificationType));
				} catch (final Exception e) {
					Logger.exception("Could not publish item modification event.", e);
				}
			} else {
				Logger.warn("Cannot publish item modification event for <null>");
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> Object getPropertyValue(final T item, final String propertyName) {
		return getPropertyValue(item, propertyName, Item.class);
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item, V> V getLocalizedPropertyValue(final T item, final String propertyName,
			final Class<V> valueType, final Locale locale) {

		final Localizable<V> localizable = (Localizable<V>) ClassUtil.getField(item, propertyName, true);

		if (localizable != null) {
			return localizable.get(locale);
		} else {
			Logger.debug(String.format("Localized property %s on type %s not found", propertyName,
					item.getClass().getSimpleName()));
		}

		return null;
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item, V> V getPropertyValue(final T item, final String propertyName, final Class<V> valueType) {
		return (V) ClassUtil.getField(item, propertyName, true);
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> void setLocalizedPropertyValue(final T item, final String propertyName,
			final Object propertyValue, final Locale locale) {

		Localizable<Object> localizable = (Localizable<Object>) ClassUtil.getField(item, propertyName, true);

		if (localizable == null) {
			Field property = ClassUtil.getFieldDefinition(item.getClass(), propertyName, true);

			if (property != null) {
				Class<?> propertyType = property.getType();

				Optional<Localizable<Object>> newLocalizable = (Optional<Localizable<Object>>) ClassUtil.instantiate(propertyType);

				if (newLocalizable.isPresent()) {
					localizable = newLocalizable.get();
				}
			}
		}

		if (localizable != null) {
			localizable.set(locale, propertyValue);
			setPropertyValue(item, propertyName, localizable);
		} else {
			Logger.debug(String.format("Localized property %s on type %s not found", propertyName,
					item.getClass().getSimpleName()));
		}
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> void setPropertyValue(final T item, final String propertyName, final Object propertyValue) {
		ClassUtil.setField(item, propertyName, propertyValue);
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> boolean isAttached(final T item) {
		return persistenceService.isAttached(item);
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> void attach(final T item) throws ModelNotFoundException {
		persistenceService.attach(item);
	}
}

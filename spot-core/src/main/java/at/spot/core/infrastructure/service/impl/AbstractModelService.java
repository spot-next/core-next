package at.spot.core.infrastructure.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.exception.ItemInterceptorException;
import at.spot.core.infrastructure.exception.ModelCreationException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.exception.ModelValidationException;
import at.spot.core.infrastructure.interceptor.ItemCreateInterceptor;
import at.spot.core.infrastructure.interceptor.ItemLoadInterceptor;
import at.spot.core.infrastructure.interceptor.ItemPrepareInterceptor;
import at.spot.core.infrastructure.interceptor.ItemRemoveInterceptor;
import at.spot.core.infrastructure.interceptor.ItemValidateInterceptor;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.service.UserService;
import at.spot.core.infrastructure.service.ValidationService;
import at.spot.core.infrastructure.support.ItemInterceptorRegistry;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.support.util.ClassUtil;
import at.spot.core.types.Item;
import at.spot.itemtype.core.user.User;
import at.spot.itemtype.core.user.UserGroup;

@SuppressWarnings("unchecked")
@Service
public abstract class AbstractModelService extends AbstractService implements ModelService {

	@Resource
	protected TypeService typeService;

	@Resource
	protected UserService<User, UserGroup> userService;

	@Resource
	protected PersistenceService persistenceService;

	@Resource
	protected ValidationService validationService;

	@Resource
	protected ItemInterceptorRegistry<ItemCreateInterceptor<Item>> itemCreateInterceptorRegistry;

	@Resource
	protected ItemInterceptorRegistry<ItemValidateInterceptor<Item>> itemValidateInterceptorRegistry;

	@Resource
	protected ItemInterceptorRegistry<ItemPrepareInterceptor<Item>> itemPrepareInterceptorRegistry;

	@Resource
	protected ItemInterceptorRegistry<ItemLoadInterceptor<Item>> itemLoadInterceptorRegistry;

	@Resource
	protected ItemInterceptorRegistry<ItemRemoveInterceptor<Item>> itemRemoveInterceptorRegistry;

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
				interceptors.stream().forEach(l -> l.onValidate(item));
			}
		}

		if (!errors.isEmpty()) {
			final ConstraintViolation<?> violation = errors.iterator().next();

			final String message = errors.stream()
					.map(v -> String.format("%s.%s %s", violation.getRootBeanClass().getSimpleName(),
							violation.getPropertyPath().toString(), violation.getMessage()))
					.collect(Collectors.joining(", "));

			throw new ModelValidationException(message, errors);
		}
	}

	protected <T extends Item> void setTypeCode(final T item) {
		ClassUtil.setField(item, "typeCode", typeService.getTypeCodeForClass(item.getClass()));
	}

	@Override
	public <T extends Item> void detach(final T... items) {
		persistenceService.detach(Arrays.asList(items));
	}

	@Override
	public <T extends Item> void detach(final List<T> items) {
		detach(items.toArray(new Item[0]));
	}

	/**
	 * Sets the {@link Item#setCreatedBy(String)} and
	 * {@link Item#setLastModifiedBy(String)}.
	 */
	public <T extends Item> void setUserInformation(final List<T> models) {
		final User currentUser = userService.getCurrentUser();

		if (currentUser == null) {
			loggingService.debug(() -> "Could not determine current session user");
		}

		final String username = currentUser != null ? currentUser.getId() : "<system>";

		for (final T model : models) {
			ClassUtil.setField(model, "createdBy", username);
			ClassUtil.setField(model, "lastModifiedBy", username);
		}
	}
}

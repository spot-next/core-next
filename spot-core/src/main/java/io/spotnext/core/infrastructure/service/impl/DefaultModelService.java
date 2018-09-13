package io.spotnext.core.infrastructure.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.event.ItemModificationEvent.ModificationType;
import io.spotnext.core.infrastructure.exception.ModelNotFoundException;
import io.spotnext.core.infrastructure.exception.ModelSaveException;
import io.spotnext.core.infrastructure.exception.ModelValidationException;
import io.spotnext.core.persistence.exception.ModelNotUniqueException;
import io.spotnext.core.persistence.query.ModelQuery;
import io.spotnext.infrastructure.type.Item;
import io.spotnext.support.util.ValidationUtil;

/**
 * <p>DefaultModelService class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
@Service
public class DefaultModelService extends AbstractModelService {

	/** {@inheritDoc} */
	@Override
	public <T extends Item> void save(final T model)
			throws ModelSaveException, ModelNotUniqueException, ModelValidationException {

		saveAll(model);
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> void saveAll(final T... models)
			throws ModelSaveException, ModelNotUniqueException, ModelValidationException {

		saveAll(Arrays.asList(models));
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> void saveAll(final List<T> models)
			throws ModelSaveException, ModelNotUniqueException, ModelValidationException {

		super.setUserInformation(models);
		super.applyPrepareInterceptors(models);
		super.validateModels(models);

		persistenceService.save(models);

		publishEvents(models, ModificationType.SAVE);
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> T get(final Class<T> type, final long pk) throws ModelNotFoundException {
		final T item = persistenceService.load(type, pk);

		if (item != null) {
			applyLoadInterceptors(Collections.singletonList(item));
			publishEvents(Collections.singletonList(item), ModificationType.LOAD);
		}

		return item;
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> T get(final Class<T> type, final Map<String, Object> searchParameters)
			throws ModelNotUniqueException {

		return get(new ModelQuery<>(type, searchParameters));
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> T get(final ModelQuery<T> query) throws ModelNotUniqueException {
		final List<T> items = getAllInternal(query);

		if (items.size() > 1) {
			throw new ModelNotUniqueException("Found more than 1 result for the given search parameters");
		}

		if (items.size() > 0) {
			applyLoadInterceptors(Collections.singletonList(items.get(0)));
			publishEvents(items, ModificationType.LOAD);

			return items.get(0);
		}

		return null;
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> List<T> getAll(final Class<T> type, final Map<String, Object> searchParameters) {
		return getAll(new ModelQuery<>(type, searchParameters));
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> List<T> getAll(final ModelQuery<T> query) {
		final List<T> items = getAllInternal(query);

		applyLoadInterceptors(items);
		publishEvents(items, ModificationType.LOAD);

		return items;
	}

	protected <T extends Item> List<T> getAllInternal(final ModelQuery<T> query) {
		final List<T> items = persistenceService.load(query);

		return items;
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> T getByExample(final T example) {
		final Map<String, Object> map = persistenceService.convertItemToMap(example);

		ValidationUtil.validateMinSize("Example item has no properties set", map.values(), 1);

		final T item = get(new ModelQuery<T>((Class<T>) example.getClass(), map));

		if (item != null) {
			publishEvents(Collections.singletonList(item), ModificationType.LOAD);
		}

		return item;
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> List<T> getAllByExample(final T example) {
		final Map<String, Object> map = persistenceService.convertItemToMap(example);

		ValidationUtil.validateMinSize("Example item has no properties set", map.values(), 1);

		final List<T> items = getAll(new ModelQuery<T>((Class<T>) example.getClass(), map));
		publishEvents(items, ModificationType.LOAD);

		return items;
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> void refresh(final T item) throws ModelNotFoundException {
		if (item == null || item.getPk() == null) {
			throw new ModelNotFoundException("Given item is null");
		}

		persistenceService.refresh(Collections.singletonList(item));
		// initialize null collections
		persistenceService.initItem(item);
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> void remove(final Class<T> type, final long pk) {
		final T itemToRemove = get(type, pk);

		removeAll(Arrays.asList(itemToRemove));
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> void removeAll(final List<T> items) throws ModelNotFoundException {
		applyRemoveInterceptors(items);
		publishEvents(items, ModificationType.REMOVE);

		persistenceService.remove(items);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Item> void remove(final T... items) throws ModelNotFoundException {
		removeAll(Arrays.asList(items));
	}

}

package io.spotnext.core.infrastructure.support;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Required;

import io.spotnext.core.infrastructure.interceptor.ItemInterceptor;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.persistence.service.PersistenceService;
import io.spotnext.infrastructure.type.Item;

/**
 * <p>ItemInterceptorRegistry class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ItemInterceptorRegistry<I extends ItemInterceptor<Item>> implements MappingRegistry<String, I> {

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected PersistenceService persistenceService;

	protected Map<String, List<I>> interceptors = new HashMap<>();

	/** {@inheritDoc} */
	@Override
	public void registerMapping(String key, I value) {
		getValues(key).add(value);
	}

	/** {@inheritDoc} */
	@Override
	public void unregisterMapping(String key, I value) {
		getValues(key).remove(value);
	}

	/**
	 * <p>getValues.</p>
	 *
	 * @param key a {@link java.lang.String} object.
	 * @return a {@link java.util.List} object.
	 */
	public List<I> getValues(String key) {
		List<I> values = interceptors.get(key);

		if (values == null) {
			values = new LinkedList<>();
			interceptors.put(key, values);
		}

		return values;
	}

	/**
	 * <p>Setter for the field <code>typeService</code>.</p>
	 *
	 * @param typeService a {@link io.spotnext.infrastructure.service.TypeService} object.
	 */
	@Required
	public void setTypeService(TypeService typeService) {
		this.typeService = typeService;
	}

	/**
	 * <p>Setter for the field <code>persistenceService</code>.</p>
	 *
	 * @param persistenceService a {@link io.spotnext.core.persistence.service.PersistenceService} object.
	 */
	@Required
	public void setPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}
}

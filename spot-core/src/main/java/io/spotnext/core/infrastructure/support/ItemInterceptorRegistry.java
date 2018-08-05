package io.spotnext.core.infrastructure.support;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Required;

import io.spotnext.core.infrastructure.interceptor.ItemInterceptor;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.persistence.service.PersistenceService;
import io.spotnext.core.types.Item;

public class ItemInterceptorRegistry<I extends ItemInterceptor<Item>> implements MappingRegistry<String, I> {

	@Resource
	protected TypeService typeService;

	@Resource
	protected PersistenceService persistenceService;

	protected Map<String, List<I>> interceptors = new HashMap<>();

	@Override
	public void registerMapping(String key, I value) {
		getValues(key).add(value);
	}

	@Override
	public void unregisterMapping(String key, I value) {
		getValues(key).remove(value);
	}

	public List<I> getValues(String key) {
		List<I> values = interceptors.get(key);

		if (values == null) {
			values = new LinkedList<>();
			interceptors.put(key, values);
		}

		return values;
	}

	@Required
	public void setTypeService(TypeService typeService) {
		this.typeService = typeService;
	}

	@Required
	public void setPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}
}

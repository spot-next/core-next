package at.spot.core.infrastructure.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.exception.PropertyNotAccessibleException;
import at.spot.core.model.Item;

@Service
public class FakeModelService extends AbstractModelService {
	Map<Long, Item> models = new HashMap<>();

	@Override
	public <T extends Item> void save(T model) throws ModelSaveException {
		Long newPK = 0l;

		if (models.size() > 0) {
			newPK = models.keySet().stream().max(Long::compare).get() + 1;
		}

		model.pk = newPK;

		this.models.put(model.pk, model);
	}

	@Override
	public <T extends Item> T get(Class<T> type, Long pk) throws ModelNotFoundException {
		Item item = models.get(pk);

		if (item != null && item.getClass().equals(type)) {
			return (T) item;
		}

		return null;
	}

	@Override
	public <T extends Item> List<T> get(Class<T> type, Map<String, Object> searchParameters)
			throws ModelNotFoundException {
		List<T> ret = new ArrayList<>();

		try {
			for (Item i : models.values()) {
				boolean found = true;

				for (String property : searchParameters.keySet()) {
					Object searchValue = searchParameters.get(property);

					Object value = i.getProperty(property);

					if (ObjectUtils.notEqual(searchValue, value)) {
						found = false;
						break;
					}
				}

				if (found) {
					ret.add((T) i);
				}
			}
		} catch (PropertyNotAccessibleException e) {
			throw new ModelNotFoundException();
		}

		return ret;
	}
}

package at.spot.core.persistence.handler;

import javax.annotation.Resource;

import at.spot.core.infrastructure.handler.ItemPropertyHandler;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.model.Item;
import at.spot.core.persistence.service.PersistenceService;

public class MapDbItemPropertyHandler implements ItemPropertyHandler {
	@Resource
	protected PersistenceService persistenceService;

	@Resource
	protected ModelService modelService;

	@Override
	public <T extends Item> Object getProperty(T item, String propertyName) {
		Object value = null;

		if (item.isPropertyInitialized(propertyName)) {
			// retrieve actual value and set it into the model
			value = persistenceService.getPropertyValue(item, propertyName);
			modelService.setPropertyValue(item, propertyName, value);

			item.setPropertyInitialized(propertyName);
		} else {
			value = modelService.getPropertyValue(item, propertyName);
		}

		return value;
	}

	@Override
	public <T extends Item, R> R getProperty(T item, String propertyName, Class<R> returnType) {
		return (R) getProperty(item, propertyName);
	}

	@Override
	public <T extends Item> void setProperty(T item, String propertyName, Object value) {
		modelService.setPropertyValue(item, propertyName, value);
		item.setPropertyInitialized(propertyName);
		item.markAsDirty(propertyName);
	}

}

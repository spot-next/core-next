package at.spot.core.persistence.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Required;

import at.spot.core.infrastructure.exception.ItemModificationListenerException;
import at.spot.core.infrastructure.interceptor.OnItemSaveListener;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.infrastructure.strategy.SerialNumberGeneratorStrategy;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.SerialNumberGeneratorException;
import at.spot.core.persistence.service.SerialNumberGeneratorService;
import at.spot.itemtype.core.UniqueIdItem;

public abstract class AbstractSerialNumberGeneratorService extends AbstractService
		implements SerialNumberGeneratorService, OnItemSaveListener {

	@Resource
	protected LoggingService logginService;

	protected Map<Class<? extends Item>, SerialNumberGeneratorStrategy<Item>> serialNumberGeneratorStrategies;

	@Override
	public <T extends UniqueIdItem> void generate(final T item) throws SerialNumberGeneratorException {

		final SerialNumberGeneratorStrategy<Item> generator = serialNumberGeneratorStrategies.get(item.getClass());

		if (generator != null) {
			generator.generate(getNextSerialNumber(item), item);
		} else {
			throw new SerialNumberGeneratorException(String
					.format("No serial number generator strategy reigstered for item of type %s", item.getClass()));
		}
	}

	abstract protected <T extends Item> Long getNextSerialNumber(final T item);

	@Override
	public <T extends Item> void onEvent(final T item) throws ItemModificationListenerException {
		if (item instanceof UniqueIdItem) {
			try {
				generate((UniqueIdItem) item);
			} catch (final SerialNumberGeneratorException e) {
				throw new ItemModificationListenerException(
						String.format("Could not generate unique id for item of type %s", item.getClass().getName()));
			}
		}
	}

	@Required
	public void setSerialNumberGeneratorStrategies(
			final Map<Class<? extends Item>, SerialNumberGeneratorStrategy<Item>> serialNumberGeneratorStrategies) {
		this.serialNumberGeneratorStrategies = serialNumberGeneratorStrategies;
	}

}
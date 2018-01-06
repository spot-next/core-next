package at.spot.core.persistence.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import at.spot.core.infrastructure.exception.ItemModificationListenerException;
import at.spot.core.infrastructure.interceptor.OnItemSaveListener;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.infrastructure.strategy.SerialNumberGeneratorStrategy;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.SerialNumberGeneratorException;
import at.spot.core.persistence.service.SerialNumberGeneratorService;
import at.spot.core.support.util.ClassUtil;
import at.spot.itemtype.core.UniqueIdItem;

public abstract class AbstractSerialNumberGeneratorService extends AbstractService
		implements SerialNumberGeneratorService, OnItemSaveListener<UniqueIdItem> {

	@Autowired(required = true)
	protected List<SerialNumberGeneratorStrategy> serialNumberGeneratorStrategies = Collections.emptyList();

	final protected Map<Class<?>, SerialNumberGeneratorStrategy<Item>> serialNumberGeneratorStrategyRegistry = new HashMap<>();

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void setup() {
		serialNumberGeneratorStrategies.forEach(l -> serialNumberGeneratorStrategyRegistry.put(l.getItemType(), l));
	}

	@Override
	public <T extends UniqueIdItem> void generate(final T item) throws SerialNumberGeneratorException {
		SerialNumberGeneratorStrategy<Item> generator = null;

		// find any suiting superclass generator
		for (Class<?> superclass : ClassUtil.getAllAssignableClasses(item.getClass())) {
			generator = serialNumberGeneratorStrategyRegistry.get(superclass);

			if (generator != null) {
				break;
			}
		}

		if (generator != null) {
			generator.generate(getNextSerialNumber(item), item);
		} else {
			throw new SerialNumberGeneratorException(String
					.format("No serial number generator strategy reigstered for item of type %s", item.getClass()));
		}
	}

	abstract protected <T extends Item> Long getNextSerialNumber(final T item);

	@Override
	public void onEvent(final UniqueIdItem item) throws ItemModificationListenerException {
		if (item instanceof UniqueIdItem) {
			try {
				generate(item);
			} catch (final SerialNumberGeneratorException e) {
				throw new ItemModificationListenerException(
						String.format("Could not generate unique id for item of type %s", item.getClass().getName()));
			}
		}
	}

	@Override
	public Class<UniqueIdItem> getItemType() {
		return UniqueIdItem.class;
	}
}
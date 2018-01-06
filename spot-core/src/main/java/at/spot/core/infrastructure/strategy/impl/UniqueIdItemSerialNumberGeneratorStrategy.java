package at.spot.core.infrastructure.strategy.impl;

import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.strategy.SerialNumberGeneratorStrategy;
import at.spot.core.persistence.exception.SerialNumberGeneratorException;
import at.spot.itemtype.core.UniqueIdItem;
import spark.utils.StringUtils;

@Service
public class UniqueIdItemSerialNumberGeneratorStrategy<T extends UniqueIdItem>
		implements SerialNumberGeneratorStrategy<T> {

	@Override
	public void generate(long nextIdValue, T item) throws SerialNumberGeneratorException {
		if (StringUtils.isBlank(item.getId())) {
			item.setId(String.format("%s-%s", item.getClass().getSimpleName(), nextIdValue));
		}
	}

	@Override
	public Class<T> getItemType() {
		return (Class<T>) UniqueIdItem.class;
	}

}

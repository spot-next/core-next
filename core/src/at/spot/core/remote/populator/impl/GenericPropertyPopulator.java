package at.spot.core.remote.populator.impl;

import org.springframework.stereotype.Component;

import at.spot.core.data.model.Item;
import at.spot.core.remote.data.GenericItemData;
import at.spot.core.remote.populator.Populator;

/**
 * Sets all properties of the source {@link Item} as property of the ItemData
 * target object.
 */
@Component
public class GenericPropertyPopulator<S extends Object, T extends GenericItemData> implements Populator<S, T> {

	@Override
	public void populate(S source, T target) {
		target.setProperty("class", source.getClass().getSimpleName());
	}
}
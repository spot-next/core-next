package at.spot.core.management.populator.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.support.ItemTypeDefinition;
import at.spot.core.infrastructure.support.ItemTypePropertyDefinition;
import at.spot.core.management.populator.Populator;
import at.spot.core.management.support.data.GenericItemDefinitionData;
import at.spot.core.model.Item;

/**
 * Sets all properties of the source {@link Item} as property of the ItemData
 * target object.
 */
@Component
public class ItemTypePopulator<S extends ItemTypeDefinition, T extends GenericItemDefinitionData>
		implements Populator<S, T> {

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected LoggingService loggingService;

	@Override
	public void populate(final S source, final T target) {
		target.setTypeCode(source.getTypeCode());
		target.setTypeName(source.getTypeName());
		target.setTypeClass(source.getTypeClass());
		target.setPackageName(source.getPackageName());

		for (final ItemTypePropertyDefinition member : source.getProperties().values()) {
			target.addProperty(member);
		}
	}
}
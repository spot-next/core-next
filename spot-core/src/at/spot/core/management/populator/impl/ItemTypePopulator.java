package at.spot.core.management.populator.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.type.ItemTypeDefinition;
import at.spot.core.infrastructure.type.ItemTypePropertyDefinition;
import at.spot.core.management.data.GenericItemDefinitionData;
import at.spot.core.management.populator.Populator;
import at.spot.core.model.Item;

/**
 * Sets all properties of the source {@link Item} as property of the ItemData
 * target object.
 */
@Component
public class ItemTypePopulator<S extends ItemTypeDefinition, T extends GenericItemDefinitionData> implements Populator<S, T> {

	@Autowired
	protected TypeService typeService;

	@Override
	public void populate(S source, T target) {
		target.typeCode = source.typeCode;
		target.typeName = source.typeName;
		target.typeClass = source.typeClass;
		target.packageName = source.packageName;
		
		for (ItemTypePropertyDefinition member : typeService.getItemTypeProperties(source.typeCode).values()) {
			target.addProperty(member);
		}
	}
}
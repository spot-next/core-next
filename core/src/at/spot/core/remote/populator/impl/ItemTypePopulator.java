package at.spot.core.remote.populator.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import at.spot.core.data.model.Item;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.type.ItemPropertyDefinition;
import at.spot.core.remote.data.GenericItemData;
import at.spot.core.remote.populator.Populator;

/**
 * Sets all properties of the source {@link Item} as property of the ItemData
 * target object.
 */
@Component
public class ItemTypePopulator<S extends Class<? extends Item>, T extends GenericItemData> implements Populator<S, T> {

	@Autowired
	protected TypeService typeService;

	@Override
	public void populate(S source, T target) {
//		Item annotation = typeService.getAnnotation(source, Item.class);
		
		target.beanName = source.getSimpleName();
		target.typeName = source.getName();
		target.typePackage = source.getPackage().getName();
		
		for (ItemPropertyDefinition member : typeService.getItemProperties(source).values()) {
			target.setProperty(member.name, member);
		}
	}
}
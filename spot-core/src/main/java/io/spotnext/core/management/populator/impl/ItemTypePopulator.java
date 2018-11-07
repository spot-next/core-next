package io.spotnext.core.management.populator.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.spotnext.core.infrastructure.service.LoggingService;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.management.populator.Populator;
import io.spotnext.core.management.support.data.GenericItemDefinitionData;
import io.spotnext.infrastructure.type.ItemTypeDefinition;
import io.spotnext.infrastructure.type.ItemTypePropertyDefinition;

/**
 * Sets all properties of the source {@link io.spotnext.infrastructure.type.Item} as property of the ItemData
 * target object.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Component
public class ItemTypePopulator<S extends ItemTypeDefinition, T extends GenericItemDefinitionData>
		implements Populator<S, T> {

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected LoggingService loggingService;

	/** {@inheritDoc} */
	@Override
	public void populate(final S source, final T target) {
		target.setTypeCode(source.getTypeCode());
		target.setTypeName(source.getTypeName());
		target.setTypeClass(source.getTypeClass());
		target.setPackageName(source.getPackageName());
		target.setAbstract(source.isAbstract());

		for (final ItemTypePropertyDefinition member : source.getProperties().values()) {
			target.addProperty(member);
		}
	}
}

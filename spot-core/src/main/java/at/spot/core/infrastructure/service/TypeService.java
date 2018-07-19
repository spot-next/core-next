package at.spot.core.infrastructure.service;

import java.util.Map;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.support.ItemTypeDefinition;
import at.spot.core.infrastructure.support.ItemTypePropertyDefinition;
import at.spot.core.types.Item;

public interface TypeService {

	/**
	 * Return a map of all concrete registered types. Abstract types are not listed
	 * here.
	 */
	Map<String, ItemTypeDefinition> getItemTypeDefinitions();

	/**
	 * Returns the item definition for the given type.
	 */
	ItemTypeDefinition getItemTypeDefinition(String typeCode) throws UnknownTypeException;

	/**
	 * Returns the class for the given type code.
	 */
	Class<? extends Item> getClassForTypeCode(String typeCode) throws UnknownTypeException;

	/**
	 * Returns the typeCode (from {@link ItemType#typeCode()} of the given class. If
	 * this property is not set, the {@link Class#getSimpleName()} is returned
	 * instead.
	 */
	<I extends Item> String getTypeCodeForClass(final Class<I> itemType);

	/**
	 * Returns a map of all the @Property annotated properties of the given item.
	 * 
	 * @param item
	 * @return Map of {@link ItemTypePropertyDefinition}, typeCode is used as key
	 */
	Map<String, ItemTypePropertyDefinition> getItemTypeProperties(String typeCode) throws UnknownTypeException;

}

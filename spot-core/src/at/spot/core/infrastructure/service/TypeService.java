package at.spot.core.infrastructure.service;

import java.util.List;
import java.util.Map;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.support.ItemTypeDefinition;
import at.spot.core.infrastructure.support.ItemTypePropertyDefinition;
import at.spot.core.model.Item;

public interface TypeService {

	/**
	 * Return a map of all concrete registered types. Abstract types are not
	 * listed here.
	 * 
	 * @return
	 */
	Map<String, ItemTypeDefinition> getItemTypeDefinitions() throws UnknownTypeException;

	/**
	 * Returns the class definition for the given type code (=bean name).
	 * 
	 * @param typeCode
	 * @return
	 */
	Class<? extends Item> getType(String typeCode) throws UnknownTypeException;

	/**
	 * Returns the typeCode (from {@link ItemType#typeCode()} of the given
	 * class. If this property is not set, the {@link Class#getSimpleName()} is
	 * returned instead.
	 * 
	 * @param itemType
	 * @return
	 * @throws UnknownTypeException
	 */
	<I extends Item> String getTypeCode(final Class<I> itemType);

	/**
	 * Returns all sub type's typeCodes.
	 * 
	 * @param typeCode
	 * @return
	 * @throws UnknownTypeException
	 */
	List<String> getAllSubTypesCodes(String typeCode, boolean includeSuperType) throws UnknownTypeException;

	/**
	 * Returns a map of all the @Property annotated properties of the given
	 * item.
	 * 
	 * @param item
	 * @return Map of {@link ItemTypePropertyDefinition}, typeCode is used as
	 *         key
	 */
	Map<String, ItemTypePropertyDefinition> getItemTypeProperties(String typeCode) throws UnknownTypeException;

	/**
	 * Returns a map of all the @Property annotated properties of the given
	 * item.
	 * 
	 * @param item
	 * @return Map of {@link ItemTypePropertyDefinition}, typeCode is used as
	 *         key
	 */
	Map<String, ItemTypePropertyDefinition> getItemTypeProperties(Class<? extends Item> itemType);

	/**
	 * Returns a map of all the @Property annotated properties of the given item
	 * that have also the @Unique annotation.
	 * 
	 * @param item
	 * @return Map of {@link ItemTypePropertyDefinition}, typeCode is used as
	 *         key
	 */
	Map<String, ItemTypePropertyDefinition> getUniqueItemTypeProperties(Class<? extends Item> itemType);

	/**
	 * Returns true if given property is set to unique for the given item type.
	 * 
	 * @param type
	 * @param propertyName
	 * @return
	 */
	boolean isPropertyUnique(Class<? extends Item> type, String propertyName);

	/**
	 * Returns the item definition for the given type.
	 * 
	 * @param typeCode
	 * @return null if there is no item found for the given type code.
	 */
	ItemTypeDefinition getItemTypeDefinition(String typeCode) throws UnknownTypeException;

}

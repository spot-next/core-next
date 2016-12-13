package at.spot.core.infrastructure.service;

import java.util.List;
import java.util.Map;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.type.ItemTypeDefinition;
import at.spot.core.infrastructure.type.ItemTypePropertyDefinition;
import at.spot.core.infrastructure.type.ModuleDefinition;
import at.spot.core.model.Item;

public interface TypeService {

	/**
	 * Scans the classpath for {@link Item} types (that are also annotated with
	 * {@link ItemType} in the given packages.
	 * 
	 * @param packages
	 */
	List<Class<? extends Item>> getItemConcreteTypes(List<ModuleDefinition> moduleDefinitions);

	/**
	 * Return a map of all concrete registered types. Abstract types are not
	 * listed here.
	 * 
	 * @return
	 */
	Map<String, ItemTypeDefinition> getItemTypeDefinitions() throws UnknownTypeException;

	/**
	 * Scans the classpath for {@link Item} types and registers them.
	 */
	void registerTypes();

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
	String getTypeCode(Class<? extends Item> itemType);

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
	 * Returns the item definition for the given type.
	 * 
	 * @param typeCode
	 * @return null if there is no item found for the given type code.
	 */
	ItemTypeDefinition getItemTypeDefinition(String typeCode) throws UnknownTypeException;
}

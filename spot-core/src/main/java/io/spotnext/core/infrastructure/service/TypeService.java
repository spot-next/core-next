package io.spotnext.core.infrastructure.service;

import java.util.Map;

import io.spotnext.core.infrastructure.exception.UnknownTypeException;
import io.spotnext.infrastructure.annotation.ItemType;
import io.spotnext.infrastructure.type.Item;
import io.spotnext.infrastructure.type.ItemTypeDefinition;
import io.spotnext.infrastructure.type.ItemTypePropertyDefinition;

/**
 * <p>TypeService interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface TypeService {

	/**
	 * Return a map of all concrete registered types. Abstract types are not
	 * listed here.
	 *
	 * @return a {@link java.util.Map} object.
	 */
	Map<String, ItemTypeDefinition> getItemTypeDefinitions();

	/**
	 * Returns the item definition for the given type.
	 *
	 * @param typeCode a {@link java.lang.String} object.
	 * @return a {@link io.spotnext.infrastructure.type.support.ItemTypeDefinition} object.
	 * @throws io.spotnext.infrastructure.exception.UnknownTypeException if any.
	 */
	ItemTypeDefinition getItemTypeDefinition(String typeCode) throws UnknownTypeException;

	/**
	 * Returns the class for the given type code.
	 *
	 * @param typeCode a {@link java.lang.String} object.
	 * @return a {@link java.lang.Class} object.
	 * @throws io.spotnext.infrastructure.exception.UnknownTypeException if any.
	 */
	Class<? extends Item> getClassForTypeCode(String typeCode) throws UnknownTypeException;

	/**
	 * Returns the typeCode (from {@link io.spotnext.infrastructure.annotation.ItemType#typeCode()} of the given
	 * class. If this property is not set, the {@link java.lang.Class#getSimpleName()} is
	 * returned instead.
	 *
	 * @param itemType a {@link java.lang.Class} object.
	 * @return a {@link java.lang.String} object.
	 */
	<I extends Item> String getTypeCodeForClass(final Class<I> itemType);

	/**
	 * Returns a map of all the @Property annotated properties of the given
	 * item.
	 *
	 * @param typeCode
	 *            of the item of which the item type properties should be
	 *            fetched.
	 * @return Map of {@link io.spotnext.infrastructure.type.support.ItemTypePropertyDefinition}, typeCode is used as
	 *         key
	 * @throws io.spotnext.infrastructure.exception.UnknownTypeException if any.
	 */
	Map<String, ItemTypePropertyDefinition> getItemTypeProperties(String typeCode) throws UnknownTypeException;

}

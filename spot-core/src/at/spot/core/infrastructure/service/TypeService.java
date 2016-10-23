package at.spot.core.infrastructure.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;

import at.spot.core.infrastructure.annotation.model.ItemType;
import at.spot.core.infrastructure.type.ItemTypeDefinition;
import at.spot.core.infrastructure.type.ItemTypePropertyDefinition;
import at.spot.core.infrastructure.type.ModuleDefinition;
import at.spot.core.model.Item;

public interface TypeService {

	/**
	 * Checks for the presence of the given annotation on the given joinPoint.
	 * 
	 * @param joinPoint
	 * @param annotation
	 * @return
	 */
	<A extends Annotation> boolean hasAnnotation(JoinPoint joinPoint, Class<A> annotation);

	/**
	 * Returns the given annotation object, if present. If the annotation is not
	 * found, null is returned.
	 * 
	 * @param joinPoint
	 * @param annotation
	 * @return
	 */
	<A extends Annotation> A getAnnotation(JoinPoint joinPoint, Class<A> annotation);

	/**
	 * Checks for the presence of the given annotation on the given class.
	 * 
	 * @param joinPoint
	 * @param annotation
	 * @return
	 */
	<A extends Annotation> boolean hasAnnotation(Class<? extends Item> type, Class<A> annotation);

	/**
	 * Returns the given annotation object, if present. If the annotation is not
	 * found, null is returned.
	 * 
	 * @param joinPoint
	 * @param annotation
	 * @return
	 */
	<A extends Annotation> A getAnnotation(Class<? extends Item> type, Class<A> annotation);

	/**
	 * Checks for the presence of the given annotation on the given member.
	 * 
	 * @param joinPoint
	 * @param annotation
	 * @return
	 */
	<A extends Annotation> boolean hasAnnotation(AccessibleObject member, Class<A> annotation);

	/**
	 * Returns the given annotation object, if present. If the annotation is not
	 * found, null is returned.
	 * 
	 * @param joinPoint
	 * @param annotation
	 * @return
	 */
	<A extends Annotation> A getAnnotation(AccessibleObject member, Class<A> annotation);

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
	Map<String, ItemTypeDefinition> getItemTypeDefinitions();

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
	Class<? extends Item> getType(String typeCode);

	/**
	 * Returns a map of all the @Property annotated properties of the given
	 * item.
	 * 
	 * @param item
	 * @return Map of {@link ItemTypePropertyDefinition}, typeCode is used as
	 *         key
	 */
	Map<String, ItemTypePropertyDefinition> getItemTypeProperties(String typeCode);

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
	 * Returns the item definition for the given type.
	 * 
	 * @param typeCode
	 * @return null if there is no item found for the given type code.
	 */
	ItemTypeDefinition getItemTypeDefinition(String typeCode);
}

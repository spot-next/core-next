package at.spot.core.infrastructure.service;

import java.lang.annotation.Annotation;
import java.util.List;

import org.aspectj.lang.JoinPoint;

import at.spot.core.infrastructure.annotation.model.Type;
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
	<A extends Annotation> boolean hasAnnotation(Class<? extends Object> type, Class<A> annotation);

	/**
	 * Returns the given annotation object, if present. If the annotation is not
	 * found, null is returned.
	 * 
	 * @param joinPoint
	 * @param annotation
	 * @return
	 */
	<A extends Annotation> A getAnnotation(Class<? extends Object> type, Class<A> annotation);

	/**
	 * Scans the classpath for {@link Item} types (that are also annotated with
	 * {@link Type} in the given packages.
	 * 
	 * @param packages
	 */
	List<Class<?>> getItemConcreteTypes(List<String> packages);
}

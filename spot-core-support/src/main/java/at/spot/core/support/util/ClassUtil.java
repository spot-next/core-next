package at.spot.core.support.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;

public class ClassUtil {

	public static List<Class<?>> getAllSuperClasses(final Class<?> type, final Class<?> stopClass,
			final boolean includeStopClass) {

		final List<Class<?>> superClasses = new LinkedList<>();

		Class<?> currentType = type;

		while (!currentType.getSuperclass().equals(stopClass)) {
			final Class<?> superClass = currentType.getSuperclass();

			superClasses.add(superClass);
			currentType = superClass;
		}

		if (includeStopClass) {
			superClasses.add(currentType.getSuperclass());
		}

		return superClasses;
	}

	/**
	 * Set the field value for the given object. This silently fails if
	 * something goes wrong. something goes wrong.
	 * 
	 * @param object
	 * @param fieldName
	 * @param value
	 */
	public static void setField(final Object object, final String fieldName, final Object value) {
		Field field;
		try {
			field = object.getClass().getField(fieldName);
			field.setAccessible(true);
			field.set(object, value);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// silently fail
		}
	}

	/**
	 * Returns the field value for the given object. This silently fails if
	 * something goes wrong. something goes wrong.
	 * 
	 * @param object
	 * @param fieldName
	 * @param value
	 */
	public static Object getField(final Object object, final String fieldName,
			final boolean includeInAccessableFields) {
		Object retVal = null;

		try {
			final Field field = object.getClass().getField(fieldName);

			if (includeInAccessableFields) {
				field.setAccessible(true);
			}

			retVal = field.get(object);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// silently fail
		}

		return retVal;
	}

	/**
	 * Invokes a method on a given object. This silently fails if something goes
	 * wrong.
	 * 
	 * @param object
	 * @param methodName
	 * @param args
	 * @return
	 */
	public static Object invokeMethod(final Object object, final String methodName, final Object... args) {
		Object retVal = null;

		final Class<?>[] paramArgs = new Class<?>[args.length];

		int i = 0;

		for (final Object arg : args) {
			paramArgs[i] = arg.getClass();
			i++;
		}

		Method method = null;

		// iterate over all superclasses and look for given method
		for (final Class<?> c : getAllAssignableClasses(object)) {
			try {
				method = c.getDeclaredMethod(methodName, paramArgs);

				if (method != null) {
					break;
				}
			} catch (IllegalArgumentException | NoSuchMethodException | SecurityException e) {
				// silently fail
			}
		}

		if (method != null) {
			method.setAccessible(true);

			try {
				retVal = method.invoke(object, args);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// silently fail
			}
		}

		return retVal;
	}

	public static <A extends Annotation> Set<Field> getFieldsWithAnnotation(final Object object,
			final Class<A> annotation) {
		final Set<Field> annotatedFields = new HashSet<>();

		for (final Class<?> c : getAllAssignableClasses(object)) {
			for (final Field field : c.getDeclaredFields()) {
				if (field.isAnnotationPresent(annotation)) {
					annotatedFields.add(field);
				}
			}
		}

		return annotatedFields;

	}

	public static List<Class<?>> getAllAssignableClasses(final Object object) {
		final List<Class<?>> classes = new ArrayList<>();
		classes.add(object.getClass());
		classes.addAll(ClassUtils.getAllSuperclasses(object.getClass()));

		return classes;
	}

	/**
	 * Checks for the presence of the given annotation on the given joinPoint.
	 * 
	 * @param joinPoint
	 * @param annotation
	 * @return
	 */
	public static <A extends Annotation> boolean hasAnnotation(final JoinPoint joinPoint, final Class<A> annotation) {
		return getAnnotation(joinPoint, annotation) != null;
	}

	/**
	 * Returns the given annotation object, if present. If the annotation is not
	 * found, null is returned.
	 * 
	 * @param joinPoint
	 * @param annotation
	 * @return
	 */
	public static <A extends Annotation> A getAnnotation(final JoinPoint joinPoint, final Class<A> annotation) {
		A ret = null;

		final Signature sig = joinPoint.getSignature();

		if (sig instanceof MethodSignature) {
			final MethodSignature methodSignature = (MethodSignature) sig;
			Method method = methodSignature.getMethod();

			if (method.getDeclaringClass().isInterface()) {
				try {
					method = joinPoint.getTarget().getClass().getMethod(methodSignature.getName());
				} catch (NoSuchMethodException | SecurityException e) {
					//
				}
			}

			ret = AnnotationUtils.findAnnotation(method, annotation);
		} else {
			final FieldSignature fieldSignature = (FieldSignature) sig;

			ret = fieldSignature.getField().getDeclaredAnnotation(annotation);
		}

		return ret;

	}

	/**
	 * Checks for the presence of the given annotation on the given class.
	 * 
	 * @param joinPoint
	 * @param annotation
	 * @return
	 */
	public static <A extends Annotation> boolean hasAnnotation(final Class<?> type, final Class<A> annotation) {
		return getAnnotation(type, annotation) != null;
	}

	/**
	 * Returns the given annotation object, if present. If the annotation is not
	 * found, null is returned.
	 * 
	 * @param joinPoint
	 * @param annotation
	 * @return
	 */
	public static <A extends Annotation> A getAnnotation(final Class<?> type, final Class<A> annotation) {
		return type.getAnnotation(annotation);
	}

	/**
	 * Checks for the presence of the given annotation on the given member.
	 * 
	 * @param joinPoint
	 * @param annotation
	 * @return
	 */
	public static <A extends Annotation> boolean hasAnnotation(final AccessibleObject member,
			final Class<A> annotation) {
		return getAnnotation(member, annotation) != null;
	}

	/**
	 * Returns the given annotation object, if present. If the annotation is not
	 * found, null is returned.
	 * 
	 * @param joinPoint
	 * @param annotation
	 * @return
	 */
	public static <A extends Annotation> A getAnnotation(final AccessibleObject member, final Class<A> annotation) {
		return member.getAnnotation(annotation);
	}
}

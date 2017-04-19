package at.spot.core.support.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * Helper utility to handle all kinds of reflection stuff.
 */
public class ClassUtil {

	private static final Logger LOG = LogManager.getLogger(ClassUtil.class);

	/**
	 * Returns a {@link Field} instance from the given {@link Class} object. If
	 * the field does not exist, null is returned.
	 * 
	 * @param type
	 * @param fieldName
	 * @param includeSuperTypes
	 *            if this is true all super classes till and including
	 *            {@link Object} will be invoked.
	 * @return
	 */
	public static Field getFieldDefinition(final Class<?> type, final String fieldName,
			final boolean includeSuperTypes) {

		assert StringUtils.isNotBlank(fieldName);

		Field field = null;

		for (final Class<?> c : getAllSuperClasses(type, Object.class, true, true)) {
			try {
				field = c.getField(fieldName);
				break;
			} catch (NoSuchFieldException | SecurityException e) {
				LOG.log(Level.INFO, String.format("Field not found: %s", fieldName));
			}
		}

		return field;
	}

	/**
	 * Returns all super classes of the given {@link Class} in the order <most
	 * concrete class> to {@link Object}.
	 * 
	 * @param type
	 * @param stopClass
	 *            {@link Object} if null
	 * @param includeStopClass
	 *            if this is true, the stop class will be included. defaults to
	 * @param includeStartClass
	 *            if this is true, the given {@link Class} is included in the
	 *            result
	 * @return a sorted list of all super classes of the given class.
	 */
	public static List<Class<?>> getAllSuperClasses(final Class<?> type, Class<?> stopClass,
			final boolean includeStopClass, final boolean includeStartClass) {

		if (stopClass == null) {
			stopClass = Object.class;
		}

		final List<Class<?>> types = new LinkedList<>();

		if (includeStartClass) {
			types.add(type);
		}

		Class<?> currentType = type;

		while (!currentType.getSuperclass().equals(stopClass)) {
			final Class<?> superClass = currentType.getSuperclass();

			types.add(superClass);
			currentType = superClass;
		}

		if (includeStopClass) {
			types.add(currentType.getSuperclass());
		}

		return types;
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
		for (final Class<?> c : getAllSuperClasses(object.getClass(), Object.class, false, true)) {
			try {
				final Field field = c.getDeclaredField(fieldName);
				field.setAccessible(true);
				field.set(object, value);
				break;
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// silently fail
				LOG.log(Level.INFO, String.format("Can't set field %s", fieldName));
			}
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

		for (final Class<?> c : getAllSuperClasses(object.getClass(), Object.class, false, true)) {
			try {
				final Field field = c.getField(fieldName);

				if (includeInAccessableFields) {
					field.setAccessible(true);
				}

				retVal = field.get(object);
				break;
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// silently fail
				LOG.log(Level.INFO, String.format("Can't set field %s", fieldName));
			}
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
				if (method != null) {
					LOG.log(Level.INFO, String.format("Can't find method %s", method.getName()));
				}
			}
		}

		if (method != null) {
			method.setAccessible(true);

			try {
				retVal = method.invoke(object, args);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// silently fail
				if (method != null) {
					LOG.log(Level.INFO, String.format("Can't invoke method %s", method.getName()));
				}
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
					// silently fail
					if (annotation != null) {
						LOG.log(Level.INFO, String.format("Can't get annotation %s", annotation.getSimpleName()));
					}
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

	public static <A extends Annotation> A getAnnotation(final Class<?> type, final String fieldName,
			final Class<A> annotation) {

		try {
			return getAnnotation(type.getField(fieldName), annotation);
		} catch (NoSuchFieldException | SecurityException e) {
			// silently ignore
			if (annotation != null) {
				LOG.log(Level.INFO, String.format("Can't get annotation %s", annotation.getSimpleName()));
			}
		}

		return null;
	}

	public static Class<?> getGenericCollectionType(final FieldSignature field) {
		return getGenericCollectionType(field.getField());
	}

	public static Class<?> getGenericCollectionType(final Field field) {
		final ParameterizedType paramType = (ParameterizedType) field.getGenericType();

		final Class<?> collectionType = (Class<?>) paramType.getActualTypeArguments()[0];

		return collectionType;
	}
}

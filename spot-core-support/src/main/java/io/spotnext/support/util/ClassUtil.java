package io.spotnext.support.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper utility to handle all kinds of reflection stuff.
 *
 * @since 1.0
 * @author mojo2012
 * @version 1.0
 */
public class ClassUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ClassUtil.class);

	/**
	 * Returns a {@link java.lang.reflect.Field} instance from the given {@link java.lang.Class} object. If the field does not exist, null is returned.
	 *
	 * @param type              a {@link java.lang.Class} object.
	 * @param fieldName         a {@link java.lang.String} object.
	 * @param includeSuperTypes if this is true all super classes till and including {@link java.lang.Object} will be invoked.
	 * @return a {@link java.lang.reflect.Field} object.
	 */
	public static Field getFieldDefinition(final Class<?> type, final String fieldName,
			final boolean includeSuperTypes) {

		assert StringUtils.isNotBlank(fieldName);

		Field field = null;

		boolean fieldRead = false;

		for (final Class<?> c : getAllSuperClasses(type, Object.class, true, true)) {
			try {
				field = c.getDeclaredField(fieldName);
				fieldRead = true;
				break;
			} catch (NoSuchFieldException | SecurityException e) {
				// ignore field
			}
		}

		if (!fieldRead) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Can't get field %s from class %s", fieldName, type.getName()));
			}
		}

		return field;
	}

	/**
	 * Returns all super classes of the given {@link java.lang.Class} in the order "most concrete class" to {@link java.lang.Object}.
	 *
	 * @param type              a {@link java.lang.Class} object.
	 * @param stopClass         {@link java.lang.Object} if null
	 * @param includeStopClass  if this is true, the stop class will be included. defaults to
	 * @param includeStartClass if this is true, the given {@link java.lang.Class} is included in the result
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

		while (currentType.getSuperclass() != null && !currentType.getSuperclass().equals(stopClass)) {
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
	 * Set the field value for the given object. This silently fails if something goes wrong. something goes wrong.
	 *
	 * @param object    a {@link java.lang.Object} object.
	 * @param fieldName a {@link java.lang.String} object.
	 * @param value     a {@link java.lang.Object} object.
	 */
	public static void setField(final Object object, final String fieldName, final Object value) {
		setField(object, fieldName, value, true);
	}

	/**
	 * Set the field value for the given object. This silently fails if something goes wrong. something goes wrong.
	 *
	 * @param object             a {@link java.lang.Object} object.
	 * @param fieldName          a {@link java.lang.String} object.
	 * @param value              a {@link java.lang.Object} object.
	 * @param includeActualClass include actual class or only super classes?
	 */
	public static void setField(final Object object, final String fieldName, final Object value, boolean includeActualClass) {
		boolean fieldSet = false;

		for (final Class<?> c : getAllSuperClasses(object.getClass(), Object.class, false, includeActualClass)) {
			try {
				final Field field = c.getDeclaredField(fieldName);
				setAccessable(field);
				field.set(object, value);
				fieldSet = true;
				break;
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				//
			}
		}

		if (!fieldSet) {
			// silently fail
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Can't set field %s from class %s", fieldName, object.getClass()));
			}
		}
	}

	/**
	 * Returns the field value for the given object. This silently fails if something goes wrong. something goes wrong.
	 *
	 * @param object                    a {@link java.lang.Object} object.
	 * @param fieldName                 a {@link java.lang.String} object.
	 * @param includeInAccessableFields a boolean.
	 * @return a {@link java.lang.Object} object.
	 */
	public static Object getField(final Object object, final String fieldName,
			final boolean includeInAccessableFields) {

		boolean fieldRead = false;
		Object retVal = null;

		for (final Class<?> c : getAllSuperClasses(object.getClass(), Object.class, false, true)) {
			try {
				final Field field = c.getDeclaredField(fieldName);

				if (includeInAccessableFields) {
					setAccessable(field);
				}

				retVal = field.get(object);
				fieldRead = true;
				break;
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// silently fail
			}
		}

		if (!fieldRead) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Can't get field %s from class %s", fieldName, object.getClass()));
			}
		}

		return retVal;
	}

	/**
	 * <p>
	 * setAccessable.
	 * </p>
	 *
	 * @param object a {@link java.lang.reflect.AccessibleObject} object.
	 */
	protected static void setAccessable(final AccessibleObject object) {
		AccessController.doPrivileged(new PrivilegedAction<Void>() {
			@Override
			public Void run() {
				object.setAccessible(true);
				return null;
			}
		});
	}

	/**
	 * Invokes a method on a given object. This silently fails if something goes wrong.
	 *
	 * @param object     a {@link java.lang.Object} object.
	 * @param methodName a {@link java.lang.String} object.
	 * @param args       a {@link java.lang.Object} object.
	 * @return a {@link java.lang.Object} object.
	 */
	public static Object invokeMethod(final Object object, final String methodName, final Object... args) {
		return invokeMethod(object, true, methodName, args);
	}

	/**
	 * Invokes a method on a given object. This silently fails if something goes wrong.
	 *
	 * @param object             a {@link java.lang.Object} object.
	 * @param includeActualClass include actual class or only super classes?
	 * @param methodName         a {@link java.lang.String} object.
	 * @param args               a {@link java.lang.Object} object.
	 * @return a {@link java.lang.Object} object.
	 */
	public static Object invokeMethod(final Object object, boolean includeActualClass, final String methodName, final Object... args) {
		Object retVal = null;

		final Class<?>[] paramArgs = new Class<?>[args.length];

		int i = 0;

		for (final Object arg : args) {
			paramArgs[i] = arg.getClass();
			i++;
		}

		Method method = null;

		// iterate over all superclasses and look for given method
		for (final Class<?> c : getAllAssignableClasses(object.getClass(), includeActualClass)) {
			try {
				final List<Method> possibleMethods = Stream.of(c.getDeclaredMethods())
						.filter(m -> methodName.equals(m.getName())).collect(Collectors.toList());

				for (final Method m : possibleMethods) {
					boolean matches = true;

					if (m.getParameterCount() == paramArgs.length) {
						for (int x = 0; x < m.getParameterCount(); x++) {
							if (!m.getParameterTypes()[x].isAssignableFrom(paramArgs[x])) {
								matches = false;
							}
						}

						if (matches) {
							method = m;
							break;
						}
					}
				}

			} catch (IllegalArgumentException | SecurityException e) {
				// silently fail
				if (LOG.isDebugEnabled()) {
					LOG.debug(String.format("Can't find method %s from class %s", methodName, c.getSimpleName()));
				}
			}

			if (method != null) {
				break;
			}
		}

		if (method != null) {
			setAccessable(method);

			try {
				retVal = method.invoke(object, args);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// silently fail
				if (LOG.isDebugEnabled()) {
					LOG.debug(String.format("Can't invoke method %s from class %s", methodName,
							method.getDeclaringClass().getSimpleName()));
				}
			}
		}

		return retVal;
	}

	/**
	 * The whole class hierarchy is searched for fields with the given annotation.
	 *
	 * @param            <A> the subtype of annotation
	 * @param type       the class to inspect
	 * @param annotation the annotation that must be present on the field
	 * @return all fields of the given class that have the given annotation.
	 */
	public static <A extends Annotation> Set<Field> getFieldsWithAnnotation(final Class<?> type,
			final Class<A> annotation) {

		return getFields(type, field -> hasAnnotation(field, annotation));
	}

	/**
	 * Returns all fields of the type and all its super types.
	 *
	 * @param type must not be null
	 * @return all fields
	 */
	public static Set<Field> getAllFields(final Class<?> type) {
		return getFields(type, null);
	}

	/**
	 * Retrieves all fields recursively that match the given filter predicate.
	 *
	 * @param type   must not be null
	 * @param filter returns true for all selected fields. If null, all fields will be returned.
	 * @return all filtered fields
	 */
	public static Set<Field> getFields(final Class<?> type, Predicate<Field> filter) {
		final Set<Field> fields = new HashSet<>();

		for (final Class<?> c : getAllAssignableClasses(type, true)) {
			for (final Field field : c.getDeclaredFields()) {
				if (filter == null || filter.test(field)) {
					fields.add(field);
				}
			}
		}

		return fields;
	}

	/**
	 * Retrieves the method with the given name
	 * 
	 * @param type       the class to inspect
	 * @param methodName of the method to retrieve
	 * @return the method with the given name, if found.
	 */
	public static Optional<Method> getMethodDefinition(Class<?> type, String methodName) {
		Set<Method> methods = getMethods(type, (m) -> m.getName().equals(methodName));

		if (methods.size() > 0) {
			return Optional.of(methods.iterator().next());
		}

		return Optional.empty();
	}

	/**
	 * Retrieves all methods recursively that match the given filter predicate.
	 *
	 * @param type   must not be null
	 * @param filter returns true for all selected methods. If null, all methods will be returned.
	 * @return all filtered methods
	 */
	public static Set<Method> getMethods(final Class<?> type, Predicate<Method> filter) {
		final Set<Method> methods = new HashSet<>();

		for (final Class<?> c : getAllAssignableClasses(type, true)) {
			for (final Method method : c.getDeclaredMethods()) {
				if (filter == null || filter.test(method)) {
					methods.add(method);
				}
			}
		}

		return methods;
	}

	/**
	 * The whole class hierarchy is searched for methods with the given annotation.
	 *
	 * @param            <A> the subtype of annotation
	 * @param type       the class to inspect
	 * @param annotation the annotation that must be present on the method
	 * @return all methods of the given class that have the given annotation.
	 */
	public static <A extends Annotation> Set<Method> getMethodsWithAnnotation(final Class<?> type,
			final Class<A> annotation) {

		final Set<Method> annotatedMethds = new HashSet<>();

		for (final Class<?> c : getAllAssignableClasses(type, true)) {
			for (final Method method : c.getDeclaredMethods()) {
				if (method.isAnnotationPresent(annotation)) {
					annotatedMethds.add(method);
				}
			}
		}

		return annotatedMethds;
	}

	/**
	 * Returns all assignable classes for the given class, starting with the actual class.
	 *
	 * @param type a {@link java.lang.Class} object.
	 * @return a {@link java.util.List} object.
	 */
	public static List<Class<?>> getAllAssignableClasses(final Class<?> type, boolean includeActualClass) {
		final List<Class<?>> classes = new ArrayList<>();

		if (includeActualClass) {
			classes.add(type);
		}

		classes.addAll(ClassUtils.getAllSuperclasses(type));

		return classes;
	}

	/**
	 * Checks for the presence of the given annotation on the given joinPoint.
	 *
	 * @param joinPoint  a {@link JoinPoint} object.
	 * @param annotation a {@link java.lang.Class} object.
	 * @return a boolean.
	 * @param <A> a A object.
	 */
	public static <A extends Annotation> boolean hasAnnotation(final JoinPoint joinPoint, final Class<A> annotation) {
		return getAnnotation(joinPoint, annotation) != null;
	}

	/**
	 * Returns the given annotation object, if present. If the annotation is not found, null is returned.
	 *
	 * @param joinPoint  a {@link JoinPoint} object.
	 * @param annotation a {@link java.lang.Class} object.
	 * @return a A object.
	 * @param <A> a A object.
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
				}
			}

			ret = method.getAnnotation(annotation);
		} else {
			final FieldSignature fieldSignature = (FieldSignature) sig;

			ret = fieldSignature.getField().getDeclaredAnnotation(annotation);
		}

		return ret;
	}

	/**
	 * Checks for the presence of the given annotation on the given class.
	 *
	 * @param type       a {@link java.lang.Class} object.
	 * @param annotation a {@link java.lang.Class} object.
	 * @return a boolean.
	 * @param <A> a A object.
	 */
	public static <A extends Annotation> boolean hasAnnotation(final Class<?> type, final Class<A> annotation) {
		return getAnnotation(type, annotation) != null;
	}

	/**
	 * Returns the given annotation object, if present. If the annotation is not found, null is returned.
	 *
	 * @param type       a {@link java.lang.Class} object.
	 * @param annotation a {@link java.lang.Class} object.
	 * @return a A object.
	 * @param <A> a A object.
	 */
	public static <A extends Annotation> A getAnnotation(final Class<?> type, final Class<A> annotation) {
		return type.getAnnotation(annotation);
	}

	/**
	 * Checks for the presence of the given annotation on the given member.
	 *
	 * @param member     a {@link java.lang.reflect.AccessibleObject} object.
	 * @param annotation a {@link java.lang.Class} object.
	 * @return a boolean.
	 * @param <A> a A object.
	 */
	public static <A extends Annotation> boolean hasAnnotation(final AccessibleObject member,
			final Class<A> annotation) {
		return member.isAnnotationPresent(annotation);
	}

	/**
	 * Returns the given annotation object, if present. If the annotation is not found, null is returned.
	 *
	 * @param member     a {@link java.lang.reflect.AccessibleObject} object.
	 * @param annotation a {@link java.lang.Class} object.
	 * @return a A object.
	 * @param <A> a A object.
	 */
	public static <A extends Annotation> A getAnnotation(final AccessibleObject member, final Class<A> annotation) {
		return member.getAnnotation(annotation);
	}

	/**
	 * <p>
	 * getAnnotation.
	 * </p>
	 *
	 * @param            <A> the generic annotation type
	 * @param type       a {@link java.lang.Class} object.
	 * @param fieldName  a {@link java.lang.String} object.
	 * @param annotation a {@link java.lang.Class} object.
	 * @return a A object.
	 */
	public static <A extends Annotation> A getAnnotation(final Class<?> type, final String fieldName,
			final Class<A> annotation) {

		try {
			return getAnnotation(getFieldDefinition(type, fieldName, true), annotation);
		} catch (final SecurityException e) {
			// silently ignore
			if (annotation != null && LOG.isDebugEnabled()) {
				LOG.debug(String.format("Can't get annotation %s for field %s from class %s",
						annotation.getSimpleName(), fieldName, type.getSimpleName()));
			}
		}

		return null;
	}

	/**
	 * <p>
	 * getGenericCollectionType.
	 * </p>
	 *
	 * @param field a {@link FieldSignature} object.
	 * @return a {@link java.lang.Class} object.
	 */
	public static Class<?> getGenericCollectionType(final FieldSignature field) {
		return getGenericCollectionType(field.getField());
	}

	/**
	 * <p>
	 * getGenericCollectionType.
	 * </p>
	 *
	 * @param field a {@link java.lang.reflect.Field} object.
	 * @return a {@link java.lang.Class} object.
	 */
	public static Class<?> getGenericCollectionType(final Field field) {
		final ParameterizedType paramType = (ParameterizedType) field.getGenericType();

		final Class<?> collectionType = (Class<?>) paramType.getActualTypeArguments()[0];

		return collectionType;
	}

	/**
	 * <p>
	 * instantiate.
	 * </p>
	 *
	 * @param type            a {@link java.lang.Class} object.
	 * @param constructorArgs a {@link java.lang.Object} object.
	 * @param                 <T> a T object.
	 * @return a {@link java.util.Optional} object.
	 */
	public static <T> Optional<T> instantiate(final Class<T> type, final Object... constructorArgs) {
		T instance = null;

		final Class<?> parentClass = type.getEnclosingClass();

		final List<Object> constructorArgValues = new ArrayList<>();
		if (constructorArgs != null) {
			constructorArgValues.addAll(Arrays.asList(constructorArgs));
		}

		final List<Class<?>> constructorArgTypes = new ArrayList<>();
		if (constructorArgs != null) {
			Stream.of(constructorArgs).forEach(a -> constructorArgTypes.add(a.getClass()));
		}

		// is inner class, first constructor argument is always the parent
		// class!
		if (parentClass != null && !Modifier.isStatic(type.getModifiers())) {
			constructorArgTypes.add(0, parentClass);
			constructorArgValues.add(0, null);
		}

		Constructor<T> matchingConstructor = null;

		for (final Constructor<?> c : type.getDeclaredConstructors()) {
			boolean found = c.getParameterCount() == constructorArgValues.size();

			for (int i = 0; i < c.getParameterTypes().length; i++) {
				if (c.getParameterCount() != constructorArgTypes.size()) {
					found = false;
					continue;
				}

				final Class<?> paramType = c.getParameterTypes()[i];

				if (i < constructorArgTypes.size()) {
					final Class<?> argumentType = constructorArgTypes.get(i);

					if (!paramType.isAssignableFrom(argumentType)) {
						found = false;
						break;
					}
				} else {
					break;
				}

			}

			if (found) {
				matchingConstructor = (Constructor<T>) c;
				break;
			} else {
				continue;
			}
		}

		if (matchingConstructor != null) {
			try {
				matchingConstructor.setAccessible(true);
				instance = matchingConstructor
						.newInstance(constructorArgValues.toArray(new Object[constructorArgValues.size()]));
			} catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				LOG.debug(e.getMessage());
				// silently ignore
			}
		}

		return Optional.ofNullable(instance);
	}

	/**
	 * <p>
	 * visitFields.
	 * </p>
	 *
	 * @param object        a {@link java.lang.Object} object.
	 * @param fieldFilter   a {@link java.util.function.Predicate} object.
	 * @param fieldConsumer a {@link java.util.function.BiConsumer} object.
	 * @param recursive     a boolean.
	 */
	public static void visitFields(Object object, Predicate<Field> fieldFilter, BiConsumer<Field, Object> fieldConsumer,
			boolean recursive) {

		for (Field field : getFields(object.getClass(), fieldFilter)) {
			if (fieldFilter == null || fieldFilter.test(field)) {
				Object fieldValue = getField(object, field.getName(), true);

				if (fieldValue != null) {
					fieldConsumer.accept(field, fieldValue);

					if (recursive) {
						visitFields(fieldValue, fieldFilter, fieldConsumer, recursive);
					}
				}
			}
		}
	}

	/**
	 * Invokes the getter method for the given property name (= field name). The method MUST not have any parameters.
	 * 
	 * @param object       to read from
	 * @param propertyName the name of the property/field
	 * @return the property value. If no matching getter is found, null is returned also.
	 */
	public static Object getProperty(Object object, String propertyName) {
		var methodName = createGetterMethodName(propertyName);
		var className = object.getClass();
		
		if (getMethodDefinition(className, methodName).isPresent()) {
			return invokeMethod(object, methodName);
		} else if (getMethodDefinition(className, propertyName).isPresent()) {
			return invokeMethod(object, propertyName);
		} else {
			return getField(object, propertyName, true);
		}
	}

	/**
	 * Invokes the setter method for the given property name (= field name). The method MUST only have one parameter.
	 * 
	 * @param object       to write to
	 * @param propertyName the name of the property/field
	 * @param value        to write to the property. Missing setters are ignored.
	 */
	public static void setProperty(Object object, String propertyName, Object value) {
		String methodName = createSetterMethodName(propertyName);

		if (getMethodDefinition(object.getClass(), methodName).isPresent()) {
			invokeMethod(object, methodName, value);
		} else {
			setField(object, propertyName, value);
		}
	}

	private static String createGetterMethodName(String propertyName) {
		return createAccessorMethodName("get", propertyName);
	}

	private static String createSetterMethodName(String propertyName) {
		return createAccessorMethodName("set", propertyName);
	}

	private static String createAccessorMethodName(String accessType, String propertyName) {
		return accessType + propertyName.substring(0, 1).toUpperCase(Locale.getDefault()) + propertyName.substring(1);
	}
}

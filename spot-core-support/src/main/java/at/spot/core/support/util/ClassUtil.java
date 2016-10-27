package at.spot.core.support.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;

public class ClassUtil {

	/**
	 * Set the field value for the given object. This silently fails if
	 * something goes wrong. something goes wrong.
	 * 
	 * @param object
	 * @param fieldName
	 * @param value
	 */
	public static void setField(Object object, String fieldName, Object value) {
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
	public static Object getPrivateField(Object object, String fieldName) {
		Object retVal = null;

		try {
			Field field = object.getClass().getField(fieldName);
			field.setAccessible(true);
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
	public static Object invokeMethod(Object object, String methodName, Object... args) {
		Object retVal = null;

		Class<?>[] paramArgs = new Class<?>[args.length];

		int i = 0;

		for (Object arg : args) {
			paramArgs[i] = arg.getClass();
			i++;
		}

		Method method = null;

		// iterate over all superclasses and look for given method
		for (Class<?> c : getAllAssignableClasses(object)) {
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

	public static <A extends Annotation> Set<Field> getFieldsWithAnnotation(Object object, Class<A> annotation) {
		Set<Field> annotatedFields = new HashSet<>();

		for (Class<?> c : getAllAssignableClasses(object)) {
			for (Field field : c.getDeclaredFields()) {
				if (field.isAnnotationPresent(annotation)) {
					annotatedFields.add(field);
				}
			}
		}

		return annotatedFields;

	}

	public static List<Class<?>> getAllAssignableClasses(Object object) {
		List<Class<?>> classes = new ArrayList<>();
		classes.add(object.getClass());
		classes.addAll(ClassUtils.getAllSuperclasses(object.getClass()));

		return classes;
	}
}

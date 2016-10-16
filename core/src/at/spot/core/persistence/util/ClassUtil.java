package at.spot.core.persistence.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassUtil {

	/**
	 * Set the field value for the given object. This silently fails if
	 * something goes wrong. something goes wrong.
	 * 
	 * @param object
	 * @param fieldName
	 * @param value
	 */
	public static void setPrivateField(Object object, String fieldName, Object value) {
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

		try {
			Method method = object.getClass().getDeclaredMethod(methodName, paramArgs);
			method.setAccessible(true);
			retVal = method.invoke(object, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			// silently fail
		}

		return retVal;
	}
}

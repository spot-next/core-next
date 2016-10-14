package at.spot.core.persistence.util;

import java.lang.reflect.Field;

public class ClassUtil {
	public static void setPrivateField(Object object, String fieldName, Object value) {
		Field field;
		try {
			field = object.getClass().getField(fieldName);
			field.setAccessible(true);
			field.set(object, value);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			//
		}
	}
}

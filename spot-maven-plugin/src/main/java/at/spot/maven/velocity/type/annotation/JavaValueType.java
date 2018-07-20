package at.spot.maven.velocity.type.annotation;

public enum JavaValueType {
	BOOLEAN, STRING, LITERAL, CLASS, ENUM_VALUE, STRING_ARRAY, LITERAL_ARRAY;

	public static JavaValueType forType(Class<?> type) {
		if (Boolean.class.isAssignableFrom(type)) {
			return BOOLEAN;
		} else if (String.class.isAssignableFrom(type)) {
			return STRING;
		} else if (String[].class.isAssignableFrom(type)) {
			return STRING_ARRAY;
		} else if (Class.class.isAssignableFrom(type)) {
			return CLASS;
		} else if (Enum.class.isAssignableFrom(type)) {
			return ENUM_VALUE;
		}

		return null;
	}

	public static JavaValueType forValue(Object instance) {
		if (instance instanceof Boolean) {
			return BOOLEAN;
		} else if (instance instanceof String) {
			return STRING;
		} else if (instance instanceof String[]) {
			return STRING_ARRAY;
		} else if (instance instanceof Class<?>) {
			return CLASS;
		} else if (instance instanceof Enum<?>) {
			return ENUM_VALUE;
		}

		return null;
	}
}

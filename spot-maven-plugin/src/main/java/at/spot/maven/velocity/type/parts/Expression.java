package at.spot.maven.velocity.type.parts;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import at.spot.maven.velocity.type.annotation.ValueType;

public class Expression {
	Object val;

	public Expression(final Object value) throws ClassNotFoundException {
		this(value, ValueType.forValue(value));
	}

	public Expression(final Object value, final Class<?> valueType) {
		this(value, ValueType.forType(valueType));
	}

	public Expression(final Object value, final ValueType valueType) {
		if (valueType == null) {
			throw new IllegalArgumentException("Value type cannot be null");
		}

		if (ValueType.BOOLEAN.equals(valueType)) {
			val = value.toString();
		} else if (ValueType.STRING.equals(valueType)) {
			val = "\"" + value + "\"";
		} else if (ValueType.CLASS.equals(valueType)) {
			val = value + ".class";
		} else if (ValueType.ENUM_VALUE.equals(valueType) && value instanceof Enum) {
			val = value.getClass().getName() + "." + ((Enum<?>) value).name();
		} else if (ValueType.LITERAL.equals(valueType)) {
			val = value;
		} else if (ValueType.STRING_ARRAY.equals(valueType)) {
			val = String.format("{%s}", StringUtils
					.join(Stream.of((String[]) value).map(e -> "\"" + e + "\"").collect(Collectors.toList()), ","));
		} else if (ValueType.LITERAL_ARRAY.equals(valueType)) {
			val = String.format("{%s}", StringUtils.join((String[]) value));
		}
	}

	@Override
	public String toString() {
		return val.toString();
	}
}

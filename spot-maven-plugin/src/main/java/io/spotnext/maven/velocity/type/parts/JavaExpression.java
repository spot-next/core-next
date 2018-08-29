package io.spotnext.maven.velocity.type.parts;

import java.io.Serializable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import io.spotnext.maven.velocity.type.annotation.JavaValueType;

/**
 * <p>JavaExpression class.</p>
 *
 * @since 1.0
 */
public class JavaExpression implements Serializable {
	private static final long serialVersionUID = 1L;
	private String value;

	/**
	 * <p>Constructor for JavaExpression.</p>
	 *
	 * @param value a {@link java.lang.Object} object.
	 * @throws java.lang.ClassNotFoundException if any.
	 */
	public JavaExpression(final Object value) throws ClassNotFoundException {
		this(value, JavaValueType.forValue(value));
	}

	/**
	 * <p>Constructor for JavaExpression.</p>
	 *
	 * @param value a {@link java.lang.Object} object.
	 * @param valueType a {@link io.spotnext.maven.velocity.type.parts.JavaMemberType} object.
	 * @throws java.lang.ClassNotFoundException if any.
	 */
	public JavaExpression(final Object value, final JavaMemberType valueType) throws ClassNotFoundException {
		this(value, JavaValueType.forType(Class.forName(valueType.getFullyQualifiedName())));
	}

	/**
	 * <p>Constructor for JavaExpression.</p>
	 *
	 * @param value a {@link java.lang.Object} object.
	 * @param valueType a {@link java.lang.Class} object.
	 */
	public JavaExpression(final Object value, final Class<?> valueType) {
		this(value, JavaValueType.forType(valueType));
	}

	/**
	 * <p>Constructor for JavaExpression.</p>
	 *
	 * @param value a {@link java.lang.Object} object.
	 * @param valueType a {@link io.spotnext.maven.velocity.type.annotation.JavaValueType} object.
	 */
	public JavaExpression(final Object value, final JavaValueType valueType) {
		if (valueType == null) {
			throw new IllegalArgumentException("Value type cannot be null");
		}

		if (JavaValueType.STRING.equals(valueType)) {
			this.value = "\"" + value + "\"";
		} else if (JavaValueType.CLASS.equals(valueType)) {
			this.value = value + ".class";
		} else if (JavaValueType.ENUM_VALUE.equals(valueType) && value instanceof Enum) {
			this.value = value.getClass().getName() + "." + ((Enum<?>) value).name();
		} else if (JavaValueType.LITERAL.equals(valueType)) {
			this.value = value.toString();
		} else if (JavaValueType.STRING_ARRAY.equals(valueType)) {
			this.value = String.format("{%s}", StringUtils
					.join(Stream.of((String[]) value).map(e -> "\"" + e + "\"").collect(Collectors.toList()), ","));
		} else if (JavaValueType.LITERAL_ARRAY.equals(valueType)) {
			this.value = String.format("{%s}", StringUtils.join((String[]) value));
		} else {
			this.value = StringUtils.EMPTY;
		}
	}

	/**
	 * <p>Getter for the field <code>value</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getValue() {
		return value;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return value;
	}
}

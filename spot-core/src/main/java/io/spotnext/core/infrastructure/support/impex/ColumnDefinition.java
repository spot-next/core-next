package io.spotnext.core.infrastructure.support.impex;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>ColumnDefinition class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ColumnDefinition {

	private String columnName;
	private final Map<String, String> modifiers = new HashMap<>();
	private String valueResolutionDescriptor;
	private Class<?> columnType;

	/**
	 * <p>getPropertyName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getPropertyName() {
		return columnName;
	}

	/**
	 * <p>setPropertyName.</p>
	 *
	 * @param propertyName a {@link java.lang.String} object.
	 */
	public void setPropertyName(String propertyName) {
		this.columnName = propertyName;
	}

	/**
	 * <p>Getter for the field <code>modifiers</code>.</p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public Map<String, String> getModifiers() {
		return modifiers;
	}

	/**
	 * <p>addModifier.</p>
	 *
	 * @param key a {@link java.lang.String} object.
	 * @param value a {@link java.lang.String} object.
	 */
	public void addModifier(String key, String value) {
		this.modifiers.put(key, value);
	}

	/**
	 * <p>addModifiers.</p>
	 *
	 * @param parseModifiers a {@link java.util.Map} object.
	 */
	public void addModifiers(Map<String, String> parseModifiers) {
		this.modifiers.putAll(parseModifiers);
	}

	/**
	 * <p>Setter for the field <code>valueResolutionDescriptor</code>.</p>
	 *
	 * @param valueResolutionDescriptor a {@link java.lang.String} object.
	 */
	public void setValueResolutionDescriptor(String valueResolutionDescriptor) {
		this.valueResolutionDescriptor = valueResolutionDescriptor;
	}

	/**
	 * <p>Getter for the field <code>valueResolutionDescriptor</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getValueResolutionDescriptor() {
		return valueResolutionDescriptor;
	}

	/**
	 * Can be null, as this is most likely not initially filled
	 *
	 * @return a {@link java.lang.Class} object.
	 */
	public Class<?> getColumnType() {
		return columnType;
	}

	/**
	 * <p>Setter for the field <code>columnType</code>.</p>
	 *
	 * @param columnType a {@link java.lang.Class} object.
	 */
	public void setColumnType(Class<?> columnType) {
		this.columnType = columnType;
	}

}

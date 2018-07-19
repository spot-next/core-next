package at.spot.core.infrastructure.support.impex;

import java.util.HashMap;
import java.util.Map;

public class ColumnDefinition {

	private String columnName;
	private final Map<String, String> modifiers = new HashMap<>();
	private String valueResolutionDescriptor;
	private Class<?> columnType;

	public String getPropertyName() {
		return columnName;
	}

	public void setPropertyName(String propertyName) {
		this.columnName = propertyName;
	}

	public Map<String, String> getModifiers() {
		return modifiers;
	}

	public void addModifier(String key, String value) {
		this.modifiers.put(key, value);
	}

	public void addModifiers(Map<String, String> parseModifiers) {
		this.modifiers.putAll(parseModifiers);
	}

	public void setValueResolutionDescriptor(String valueResolutionDescriptor) {
		this.valueResolutionDescriptor = valueResolutionDescriptor;
	}

	public String getValueResolutionDescriptor() {
		return valueResolutionDescriptor;
	}

	/**
	 * Can be null, as this is most likely not initially filled
	 */
	public Class<?> getColumnType() {
		return columnType;
	}

	public void setColumnType(Class<?> columnType) {
		this.columnType = columnType;
	}

}

package at.spot.core.infrastructure.support.impex;

import java.util.HashMap;
import java.util.Map;

public class ColumnDefinition {

	private String propertyName;
	private final Map<String, String> modifiers = new HashMap<>();
	private String valueResolutionDescriptor;

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public Map<String, String> getModifiers() {
		return modifiers;
	}

	public void addModifier(String key, String value) {
		this.modifiers.put(key, value);
	}

	public void addModifiers(Map<String, String> parseModifiers) {
		this.modifiers.putAll(modifiers);
	}

	public void setValueResolutionDescriptor(String valueResolutionDescriptor) {
		this.valueResolutionDescriptor = valueResolutionDescriptor;
	}

	public String getValueResolutionDescriptor() {
		return valueResolutionDescriptor;
	}

}

package io.spotnext.infrastructure.type;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;

/**
 * Represents an item type definition.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ItemTypeDefinition {

	protected final String typeCode;
	protected final String typeClass;
	protected final String typeName;
	protected final String packageName;
	protected final Map<String, ItemTypePropertyDefinition> properties;

	/**
	 * <p>Constructor for ItemTypeDefinition.</p>
	 *
	 * @param typeCode a {@link java.lang.String} object.
	 * @param typeClass a {@link java.lang.String} object.
	 * @param typeName a {@link java.lang.String} object.
	 * @param packageName a {@link java.lang.String} object.
	 * @param properties a {@link java.util.Map} object.
	 */
	public ItemTypeDefinition(String typeCode, String typeClass, String typeName, String packageName,
			Map<String, ItemTypePropertyDefinition> properties) {

		this.typeCode = typeCode;
		this.typeClass = typeClass;
		this.typeName = typeName;
		this.packageName = packageName;
		this.properties = properties;
	}

	/**
	 * <p>Getter for the field <code>typeCode</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getTypeCode() {
		return typeCode;
	}

	/**
	 * <p>Getter for the field <code>typeClass</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getTypeClass() {
		return typeClass;
	}

	/**
	 * <p>Getter for the field <code>typeName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * <p>Getter for the field <code>packageName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * <p>Getter for the field <code>properties</code>.</p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public Map<String, ItemTypePropertyDefinition> getProperties() {
		return MapUtils.unmodifiableMap(properties);
	}
}

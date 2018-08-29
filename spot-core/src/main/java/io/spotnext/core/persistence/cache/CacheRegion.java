package io.spotnext.core.persistence.cache;

/**
 * <p>CacheRegion class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class CacheRegion<K, V> {
	private String name;
	private Class<K> keyType;
	private Class<V> valueType;

	/**
	 * The name of the cache region.
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		return name;
	}

	/**
	 * <p>Setter for the field <code>name</code>.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * <p>Getter for the field <code>keyType</code>.</p>
	 *
	 * @return a {@link java.lang.Class} object.
	 */
	public Class<K> getKeyType() {
		return keyType;
	}

	/**
	 * <p>Setter for the field <code>keyType</code>.</p>
	 *
	 * @param keyType a {@link java.lang.Class} object.
	 */
	public void setKeyType(Class<K> keyType) {
		this.keyType = keyType;
	}

	/**
	 * <p>Getter for the field <code>valueType</code>.</p>
	 *
	 * @return a {@link java.lang.Class} object.
	 */
	public Class<V> getValueType() {
		return valueType;
	}

	/**
	 * <p>Setter for the field <code>valueType</code>.</p>
	 *
	 * @param valueType a {@link java.lang.Class} object.
	 */
	public void setValueType(Class<V> valueType) {
		this.valueType = valueType;
	}

}

package at.spot.core.persistence.cache;

public class CacheRegion<K, V> {
	private String name;
	private Class<K> keyType;
	private Class<V> valueType;

	/**
	 * The name of the cache region.
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<K> getKeyType() {
		return keyType;
	}

	public void setKeyType(Class<K> keyType) {
		this.keyType = keyType;
	}

	public Class<V> getValueType() {
		return valueType;
	}

	public void setValueType(Class<V> valueType) {
		this.valueType = valueType;
	}

}

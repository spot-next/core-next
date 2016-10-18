package at.spot.core.infrastructure.type;

/**
 * A wrapper for a primitive object. 
 *
 */
public class Value<T> {
	protected T object;

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}
}

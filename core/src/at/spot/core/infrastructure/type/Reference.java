package at.spot.core.infrastructure.type;

/**
 * A wrapper for a complex java object. 
 *
 */
public class Reference<T> {
	protected T object;

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}
}

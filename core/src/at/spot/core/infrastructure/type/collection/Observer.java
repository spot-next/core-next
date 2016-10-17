package at.spot.core.infrastructure.type.collection;

/**
 * Defines the interface for an observer for {@link ObservableList} collections.
 *
 */
public interface Observer {

	/**
	 * Notifies the observer about the change that occurred.
	 *
	 * @param collectionName
	 *            the internal name of the collection
	 * @param change
	 *            the type of change that occurred
	 * @param element
	 *            the element that has been affected by the change
	 */
	void notify(String collectionName, ObservableChange change, Object element);
}

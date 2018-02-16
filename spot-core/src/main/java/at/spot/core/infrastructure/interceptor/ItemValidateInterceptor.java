package at.spot.core.infrastructure.interceptor;

import at.spot.core.infrastructure.exception.ItemInterceptorException;
import at.spot.core.infrastructure.exception.ModelValidationException;
import at.spot.core.model.Item;

public interface ItemValidateInterceptor<T extends Item> extends ItemInterceptor<T> {
	/**
	 * The given item has not been saved by the the persistence layer but already
	 * passed the (JSR 303) annotation-based validation.
	 * 
	 * @param item
	 *            The item to be saved
	 * @throws ModelValidationException
	 *             Can be thrown if the item is not valid
	 * @throws ItemInterceptorException
	 *             If thrown the item will not be persisted
	 */
	void onValidate(T item) throws ModelValidationException, ItemInterceptorException;
}

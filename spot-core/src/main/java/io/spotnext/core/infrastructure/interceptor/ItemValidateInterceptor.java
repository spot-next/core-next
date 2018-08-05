package io.spotnext.core.infrastructure.interceptor;

import io.spotnext.core.infrastructure.exception.ModelValidationException;
import io.spotnext.core.types.Item;

public interface ItemValidateInterceptor<T extends Item> extends ItemInterceptor<T> {
	/**
	 * The given item has not been saved by the the persistence layer but already
	 * passed the (JSR 303) annotation-based validation.
	 * 
	 * @param item
	 *            The item to be saved
	 * @throws ModelValidationException
	 *             Can be thrown if the item is not valid
	 */
	void onValidate(T item) throws ModelValidationException;
}

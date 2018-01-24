package at.spot.core.infrastructure.interceptor;

import at.spot.core.infrastructure.exception.ItemInterceptorException;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.model.Item;

public interface ItemSaveInterceptor<T extends Item> extends ItemInterceptor<T> {
	/**
	 * The given item has been validated and can now be post-processed before it is
	 * persisted by the persistence layer.
	 * 
	 * @param item
	 *            the validated item
	 * @throws ItemInterceptorException
	 *             if thrown this will not be handler by the {@link ModelService}
	 */
	void onSave(T item) throws ItemInterceptorException;
}

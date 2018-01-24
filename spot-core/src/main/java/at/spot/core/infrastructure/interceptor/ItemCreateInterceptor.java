package at.spot.core.infrastructure.interceptor;

import at.spot.core.infrastructure.exception.ItemInterceptorException;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.model.Item;

public interface ItemCreateInterceptor<T extends Item> extends ItemInterceptor<T> {
	/**
	 * The newly created item has been intialized from the persistence layer.
	 * Further initialization (eg. default values) can be set here.
	 * 
	 * @param item
	 *            the newly instantiated item instance
	 * @throws ItemInterceptorException
	 *             if thrown this will not be handler by the {@link ModelService}
	 */
	void onCreate(T item) throws ItemInterceptorException;
}

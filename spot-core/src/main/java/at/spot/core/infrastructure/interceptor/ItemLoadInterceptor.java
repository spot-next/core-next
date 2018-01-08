package at.spot.core.infrastructure.interceptor;

import at.spot.core.infrastructure.exception.ItemInterceptorException;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.model.Item;

public interface ItemLoadInterceptor<T extends Item> extends ItemInterceptor<T> {
	/**
	 * The given item has been loaded from the persistence layer. Further processing
	 * can be done here.
	 * 
	 * @param item
	 *            the loaded item
	 * @throws ItemInterceptorException
	 *             if thrown this will not be handler by the {@link ModelService}
	 */
	void onLoad(T item) throws ItemInterceptorException;
}

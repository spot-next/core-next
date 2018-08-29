package io.spotnext.commerce.service;

import io.spotnext.itemtype.commerce.store.BaseStore;

/**
 * Provides functionality around the base store.
 *
 */
public interface BaseStoreService {
	/**
	 * Sets the given {@link io.spotnext.itemtype.commerce.store.BaseStore} in the current user session.
	 *
	 * @param baseStore a {@link io.spotnext.itemtype.commerce.store.BaseStore} object.
	 */
	void setSessionBaseStore(BaseStore baseStore);

	/**
	 * Returns the {@link io.spotnext.itemtype.commerce.store.BaseStore} of the current user session.
	 *
	 * @return a {@link io.spotnext.itemtype.commerce.store.BaseStore} object.
	 */
	BaseStore getSessionBaseStore();

	/**
	 * Returns the {@link io.spotnext.itemtype.commerce.store.BaseStore} with the given id or null if not found.
	 *
	 * @param id a {@link java.lang.String} object.
	 * @return a {@link io.spotnext.itemtype.commerce.store.BaseStore} object.
	 */
	BaseStore getBaseStoreById(String id);
}

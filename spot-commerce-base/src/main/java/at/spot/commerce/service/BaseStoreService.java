package at.spot.commerce.service;

import at.spot.itemtype.commerce.store.BaseStore;

/**
 * Provides functionality around the base store.
 *
 */
public interface BaseStoreService {
	/**
	 * Sets the given {@link BaseStore} in the current user session.
	 * 
	 * @param baseStore
	 */
	void setSessionBaseStore(BaseStore baseStore);

	/**
	 * Returns the {@link BaseStore} of the current user session.
	 * 
	 */
	BaseStore getSessionBaseStore();

	/**
	 * Returns the {@link BaseStore} with the given id or null if not found.
	 * 
	 * @param id
	 */
	BaseStore getBaseStoreById(String id);
}

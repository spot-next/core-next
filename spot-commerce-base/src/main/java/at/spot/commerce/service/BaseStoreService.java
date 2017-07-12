package at.spot.commerce.service;

import at.spot.itemtype.commerce.store.BaseStore;

/**
 * Provides functionality around the base store.
 *
 */
public interface BaseStoreService {
	/**
	 * Sets the given {@link BaseStore} in the currenet user session.
	 * 
	 * @param baseStore
	 */
	void setSessionBaseStore(BaseStore baseStore);

	/**
	 * Returns the {@link BaseStore} of the current user session.
	 * 
	 * @return
	 */
	BaseStore getSessionBaseStore();

	/**
	 * Returns the {@link BaseStore} with the given id or null if not found.
	 * 
	 * @param id
	 * @return
	 */
	BaseStore getBaseStoreById(String id);
}

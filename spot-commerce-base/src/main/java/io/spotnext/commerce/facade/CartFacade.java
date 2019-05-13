package io.spotnext.commerce.facade;

import io.spotnext.itemtype.commerce.order.CartData;
import io.spotnext.itemtype.commerce.order.CartModificationResult;

/**
 * <p>
 * CheckoutFacade interface.
 * </p>
 */
public interface CartFacade {
	/**
	 * Adds the product with the given ID and quantity to the current session cart
	 * 
	 * @param productId
	 * @param quantity
	 */
	CartModificationResult addToCart(String productId, int quantity);

	/**
	 * Returns the current session cart (creates a new one, if it doesn't exist)
	 * 
	 * @return can never be null
	 */
	CartData getCurrentCart();

	/**
	 * @return the amount of all items in the current session cart.
	 */
	int getCurrentAmountOfCartItems();
}

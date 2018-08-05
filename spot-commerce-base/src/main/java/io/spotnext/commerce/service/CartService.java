package io.spotnext.commerce.service;

import io.spotnext.itemtype.commerce.catalog.Product;
import io.spotnext.itemtype.commerce.order.Cart;
import io.spotnext.itemtype.commerce.order.CartEntry;

/**
 * The cart service provides basic functionality for handling user carts.
 */
public interface CartService {

	/**
	 * Returns the cart of the current session. If no cart has been created yet,
	 * a new one is created and added to the session.
	 * 
	 */
	Cart getSessionCart();

	/**
	 * Removes the cart from the current session.
	 */
	void removeSessionCart();

	/**
	 * Create a new session cart, overriding the current cart in the session, if
	 * existing.
	 * 
	 */
	Cart createNewSessionCart();

	/**
	 * Adds a new product to the cart.
	 * 
	 * @param cart
	 * @param product
	 * @param quantity
	 */
	CartEntry addToCart(Cart cart, Product product, int quantity);

	/**
	 * Removes a product from the cart.
	 * 
	 * @param cart
	 * @param product
	 */
	CartEntry reomveFromCart(Cart cart, Product product);

	/**
	 * Removes the cart entry with the give entry number.
	 * 
	 * @param cart
	 * @param entryNumber
	 */
	CartEntry reomveFromCart(Cart cart, int entryNumber);

	/**
	 * Updates the quantity of the cart entry. If the quantity is set to 0,
	 * depending on the {@link CartService} implementation, the cart entry might
	 * be removed as well.
	 * 
	 * @param cart
	 * @param entryNumber
	 * @param product
	 * @param quantity
	 */
	CartEntry updateCart(Cart cart, int entryNumber, Product product, int quantity);
}

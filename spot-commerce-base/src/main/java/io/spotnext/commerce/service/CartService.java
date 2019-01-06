package io.spotnext.commerce.service;

import java.util.Optional;

import io.spotnext.itemtype.commerce.catalog.Product;
import io.spotnext.itemtype.commerce.order.Cart;
import io.spotnext.itemtype.commerce.order.CartEntry;
import io.spotnext.itemtype.commerce.order.CartModification;
import io.spotnext.itemtype.commerce.order.CartModificationResult;

/**
 * The cart service provides basic functionality for handling user carts.
 */
public interface CartService {

	/**
	 * Returns the cart with the given uid..
	 *
	 * @return a {@link io.spotnext.itemtype.commerce.order.Cart} object.
	 */
	Optional<Cart> getCart(String uid);

	/**
	 * Returns the cart of the current session. If no cart has been created yet, a new one is created and added to the session.
	 *
	 * @param create a new cart if no session cart is available
	 * @return a {@link io.spotnext.itemtype.commerce.order.Cart} object.
	 */
	Optional<Cart> getSessionCart(boolean createIfNecessary);

	/**
	 * Removes the cart from the current session.
	 */
	void removeSessionCart();

	/**
	 * Create a new session cart, overriding the current cart in the session, if existing.
	 *
	 * @return a {@link io.spotnext.itemtype.commerce.order.Cart} object.
	 */
	Cart createNewSessionCart();

	/**
	 * Adds a new product to the cart.
	 *
	 * @param modificationData the DTO containing the information about the desired cart modification
	 * @return the created or updated cart entry.
	 */
	CartModificationResult updateCart(CartModification modificationData);

	/**
	 * Adds a new product to the cart.
	 *
	 * @param cart      to be manipulated
	 * @param productId of the product to add
	 * @param quantity  of the product to add
	 * @return the created cart entry
	 */
	CartEntry addToCart(Cart cart, String productId, int quantity);

	/**
	 * Removes a product from the cart.
	 *
	 * @param cart    a {@link io.spotnext.itemtype.commerce.order.Cart} object.
	 * @param product a {@link io.spotnext.itemtype.commerce.catalog.Product} object.
	 * @return a {@link io.spotnext.itemtype.commerce.order.CartEntry} object.
	 */
	CartEntry removeFromCart(Cart cart, Product product);

	/**
	 * Removes the cart entry with the give entry number.
	 *
	 * @param cart        a {@link io.spotnext.itemtype.commerce.order.Cart} object.
	 * @param entryNumber a int.
	 * @return a {@link io.spotnext.itemtype.commerce.order.CartEntry} object.
	 */
	CartEntry removeFromCart(Cart cart, int entryNumber);

	/**
	 * Updates the quantity of the cart entry. If the quantity is set to 0, depending on the {@link io.spotnext.commerce.service.CartService} implementation,
	 * the cart entry might be removed as well.
	 *
	 * @param cart        a {@link io.spotnext.itemtype.commerce.order.Cart} object.
	 * @param entryNumber a int.
	 * @param product     a {@link io.spotnext.itemtype.commerce.catalog.Product} object.
	 * @param quantity    a int.
	 * @return a {@link io.spotnext.itemtype.commerce.order.CartEntry} object.
	 */
	CartEntry updateCart(Cart cart, int entryNumber, Product product, int quantity);
}

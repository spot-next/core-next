package at.spot.commerce.service;

import at.spot.commerce.model.catalog.Product;
import at.spot.commerce.model.order.Cart;
import at.spot.commerce.model.order.CartEntry;

/**
 * The cart service provides basic functionality for handling user carts.
 */
public interface CartService {

	Cart getSessionCart();

	void removeSessionCart();

	Cart createNewSessionCart();

	CartEntry addToCart(Cart cart, Product product, int quantity);

	CartEntry reomveFromCart(Cart cart, Product product);

	CartEntry reomveFromCart(Cart cart, int entryNumber);

	CartEntry reomveFromCart(Cart cart, CartEntry entry);

	CartEntry updateCart(Cart cart, int entryNumber, Product product, int quantity);
}

package io.spotnext.commerce.service.impl;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import io.spotnext.commerce.service.CartService;
import io.spotnext.core.infrastructure.exception.ModelNotFoundException;
import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.infrastructure.service.SessionService;
import io.spotnext.core.infrastructure.service.UserService;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.persistence.query.ModelQuery;
import io.spotnext.itemtype.commerce.catalog.Product;
import io.spotnext.itemtype.commerce.order.Cart;
import io.spotnext.itemtype.commerce.order.CartEntry;
import io.spotnext.itemtype.core.beans.UserData;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;

public class DefaultCartService extends AbstractService implements CartService {

	@Autowired
	protected ModelService modeService;

	@Autowired
	protected UserService<User, UserGroup> userService;

	@Autowired
	protected SessionService sessionService;

	@Autowired

	@Override
	public Optional<Cart> getCart(String uid) {
		final ModelQuery<Cart> query = new ModelQuery<>(Cart.class, Collections.singletonMap("uid", uid));

		Cart cart = null;

		try {
			cart = modelService.get(query);
		} catch (ModelNotFoundException e) {
			// ignore
		}

		return Optional.ofNullable(cart);
	}

	@Override
	public Optional<Cart> getSessionCart(boolean createIfNecessary) {
		String cartId = (String) sessionService.getCurrentSession().getAttribute("cart");

		Cart cart = null;

		if (StringUtils.isNotBlank(cartId)) {
			cart = getCart(cartId).orElse(null);
		}

		if (cart == null && createIfNecessary) {
			cart = createNewSessionCart();
		}

		return Optional.ofNullable(cart);
	}

	@Override
	public void removeSessionCart() {
		final Optional<Cart> cart = getSessionCart(false);

		if (cart.isPresent()) {
			modelService.remove(cart.get());
			sessionService.getCurrentSession().setAttribute("cart", null);
		}
	}

	@Override
	public Cart createNewSessionCart() {
		final UserData customerData = userService.getCurrentUser();
		final User customer = userService.getUser(customerData.getUid());

		final Cart cart = modelService.create(Cart.class);
		cart.setUid(UUID.randomUUID().toString());
		cart.setCustomer(customer);

		modelService.save(cart);
		sessionService.getCurrentSession().setAttribute("cart", cart.getUid());

		return cart;
	}

	@Override
	public CartEntry addToCart(Cart cart, Product product, int quantity) {

		return null;
	}

	@Override
	public CartEntry removeFromCart(Cart cart, Product product) {
		return null;
	}

	@Override
	public CartEntry removeFromCart(Cart cart, int entryNumber) {
		return null;
	}

	@Override
	public CartEntry updateCart(Cart cart, int entryNumber, Product product, int quantity) {
		return null;
	}

}

package io.spotnext.commerce.facade.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import io.spotnext.commerce.facade.CartFacade;
import io.spotnext.commerce.service.CartService;
import io.spotnext.itemtype.commerce.enumeration.CartModificationOperation;
import io.spotnext.itemtype.commerce.order.AbstractOrderEntry;
import io.spotnext.itemtype.commerce.order.AbstractOrderEntryData;
import io.spotnext.itemtype.commerce.order.Cart;
import io.spotnext.itemtype.commerce.order.CartData;
import io.spotnext.itemtype.commerce.order.CartEntryData;
import io.spotnext.itemtype.commerce.order.CartModification;
import io.spotnext.itemtype.commerce.order.CartModificationResult;
import io.spotnext.itemtype.commerce.order.ProductData;
import static io.spotnext.support.util.MiscUtil.$;

public class DefaultCartFacade extends AbstractFacade implements CartFacade {

	@Autowired
	@Qualifier("cartService")
	private CartService cartService;

	@Override
	public CartModificationResult addToCart(String productId, int quantity) {
		final Optional<Cart> cart = cartService.getSessionCart(true);
		final Cart currentCart = cart.get();

		final CartModification modification = new CartModification();
		modification.setCartId(currentCart.getUid());
		modification.setOperation(CartModificationOperation.ADD_ENTRY);
		modification.setProductId(productId);
		modification.setQuantity(quantity);

		return cartService.updateCart(modification);
	}

	@Override
	public CartData getCurrentCart() {
		return convertCart(cartService.getSessionCart(true).get());
	}

	private CartData convertCart(Cart cart) {
		CartData cartData = new CartData();

		cartData.setUid(cart.getUid());
		cartData.setEntries((List<AbstractOrderEntryData>) cart.getEntries().stream().map(this::convertEntry).collect(Collectors.toList()));

		return cartData;
	}

	private AbstractOrderEntryData convertEntry(AbstractOrderEntry entry) {
		CartEntryData data = new CartEntryData();

		data.setEntryNumber(entry.getEntryNumber());
		data.setQuantity(entry.getQuantity());

		ProductData product = new ProductData();
		product.setUid(entry.getProduct().getUid());
		product.setName($(() -> entry.getProduct().getName().get(), null));
		product.setDescription($(() -> entry.getProduct().getDescription().get(), null));

		data.setProduct(product);

		return data;
	}
}

package io.spotnext.commerce.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.commerce.exception.NoSuchCartEntryException;
import io.spotnext.commerce.exception.NoSuchCartException;
import io.spotnext.commerce.service.CartService;
import io.spotnext.commerce.service.ProductService;
import io.spotnext.core.infrastructure.exception.ModelNotFoundException;
import io.spotnext.core.infrastructure.service.SessionService;
import io.spotnext.core.infrastructure.service.UserService;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.persistence.query.ModelQuery;
import io.spotnext.itemtype.commerce.catalog.Product;
import io.spotnext.itemtype.commerce.order.AbstractOrderEntry;
import io.spotnext.itemtype.commerce.order.Cart;
import io.spotnext.itemtype.commerce.order.CartEntry;
import io.spotnext.itemtype.commerce.order.CartModification;
import io.spotnext.itemtype.commerce.order.CartModificationResult;
import io.spotnext.itemtype.core.beans.UserData;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;

public class DefaultCartService extends AbstractService implements CartService {

	@Autowired
	protected UserService<User, UserGroup> userService;

	@Autowired
	protected SessionService sessionService;

	@Autowired
	protected ProductService productService;

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
		Optional<String> cartId = sessionService.getCurrentSession().<String>attribute("cart") //
				.filter(s -> StringUtils.isNotBlank(s));

		Cart cart = null;

		if (cartId.isPresent()) {
			cart = getCart(cartId.get()).orElse(null);
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
	public CartEntry addToCart(Cart cart, String productId, int quantity) {
		final CartEntry entry = modelService.create(CartEntry.class);

		return updateEntry(cart, entry, productId, quantity);
	}

	@Override
	public CartEntry removeFromCart(Cart cart, Product product) {
		return null;
	}

	@Override
	public CartEntry removeFromCart(Cart cart, int entryNumber) {
		final List<CartEntry> entriesToDelete = cart.getEntries().stream() //
				.filter(e -> e.getEntryNumber() == entryNumber) //
				.map(e -> (CartEntry) e) //
				.collect(Collectors.toList());

		if (entriesToDelete.size() == 1) {
			cart.getEntries().removeAll(entriesToDelete);
			return entriesToDelete.get(0);
		}

		throw new NoSuchCartEntryException(cart.getUid(), entryNumber);
	}

	protected CartEntry updateEntry(Cart cart, CartEntry entry, String productId, int quantity) {
		int highestOrderEntryNumber = cart.getEntries().stream().map(e -> e.getEntryNumber()).max(Integer::compare).orElse(0);
		entry.setEntryNumber(highestOrderEntryNumber + 1);
		entry.setProduct(productService.getProductForId(productId).orElseThrow());
		entry.setQuantity(quantity);
		entry.setOrder(cart);

		modelService.save(entry);

		return entry;
	}

	@Override
	public CartEntry updateCart(Cart cart, int entryNumber, String productId, int quantity) {
		Optional<CartEntry> entry = cart.getEntries().stream() //
				.filter(e -> e instanceof CartEntry) //
				.map(e -> (CartEntry) e)
				.filter(e -> e.getEntryNumber() == entryNumber).findFirst();

		if (entry.isPresent()) {
			return updateEntry(cart, entry.get(), productId, quantity);
		} else {
			return addToCart(cart, productId, quantity);
		}
	}

	@Override
	@SuppressFBWarnings("SF_SWITCH_FALLTHROUGH")
	public CartModificationResult updateCart(CartModification modificationData) throws NoSuchCartException {
		Cart cart = getCart(modificationData.getCartId()).orElseThrow(() -> new NoSuchCartException(modificationData.getCartId()));
		Optional<CartEntry> entry = getCartEntryToModify(cart, modificationData);

		CartModificationResult result = new CartModificationResult();

		switch (modificationData.getOperation()) {
		case UPDATE_ENTRY:
			if (modificationData.getQuantity() > 0 && entry.isPresent()) {
				addModifiedCartEntries(result,
						updateCart(cart, entry.get().getEntryNumber(), entry.get().getProduct().getUid(), modificationData.getQuantity()));
				break;
			}

			// fall through in case an entry number was specified
		case ADD_ENTRY:
			if (modificationData.getQuantity() > 0) {
				addModifiedCartEntries(result, addToCart(cart, modificationData.getProductId(), modificationData.getQuantity()));
			}

			// fall through because quantity 0 also means delete!
		case REMOVE_ENTRY:
			if (entry.isPresent()) {
				addModifiedCartEntries(result, removeFromCart(cart, entry.get().getEntryNumber()));
			}
			break;
		default:
			break;
		}

		return result;
	}

	private void addModifiedCartEntries(CartModificationResult result, AbstractOrderEntry entry) {
		if (result.getModifiedCartEntries() == null) {
			result.setModifiedCartEntries(new ArrayList<>());
		}

		if (entry != null) {
			result.getModifiedCartEntries().add(entry);
		}
	}

	private Optional<CartEntry> getCartEntryToModify(Cart cart, CartModification modificationData) {
		if (modificationData.getCartEntryNumber() == null) {
			if (StringUtils.isNotBlank(modificationData.getProductId())) {
				return cart.getEntries().stream() //
						.filter(e -> modificationData.getProductId().equals(e.getProduct().getUid())) //
						.map(e -> (CartEntry) e) //
						.findFirst();
			} else {
				return Optional.empty();
			}
		}

		return cart.getEntries().stream().filter(e -> modificationData.getCartEntryNumber().equals(e.getEntryNumber())).map(e -> (CartEntry) e).findFirst();
	}
}

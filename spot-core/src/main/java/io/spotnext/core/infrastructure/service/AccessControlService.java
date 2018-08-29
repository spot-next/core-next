package io.spotnext.core.infrastructure.service;

import io.spotnext.core.types.Item;
import io.spotnext.itemtype.core.user.User;

/**
 * This service uses the {@link io.spotnext.itemtype.core.user.User} registered assigned to the current session
 * and evaluates access permissions for {@link io.spotnext.core.types.Item} types and instances.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface AccessControlService {

	/**
	 * Returns true if the current session user is allowed to access the given
	 * {@link io.spotnext.core.types.Item} type.
	 *
	 * @param type a {@link java.lang.Class} object.
	 * @param <T> a T object.
	 * @return a boolean.
	 */
	<T extends Item> boolean accessAllowed(Class<T> type);

	/**
	 * Returns true if the current session user is allowed to access the given
	 * property of the given {@link io.spotnext.core.types.Item} type.
	 *
	 * @param type a {@link java.lang.Class} object.
	 * @param property a {@link java.lang.String} object.
	 * @param <T> a T object.
	 * @return a boolean.
	 */
	<T extends Item> boolean accessAllowed(Class<T> type, String property);

	/**
	 * Returns true if the current session user is allowed to access the given
	 * {@link io.spotnext.core.types.Item} instance.
	 *
	 * @param type a T object.
	 * @param <T> a T object.
	 * @return a boolean.
	 */
	<T extends Item> boolean accessAllowed(T type);

	/**
	 * Returns true if the current session user is allowed to access the given
	 * property of the given {@link io.spotnext.core.types.Item} instance.
	 *
	 * @param type a T object.
	 * @param property a {@link java.lang.String} object.
	 * @param <T> a T object.
	 * @return a boolean.
	 */
	<T extends Item> boolean accessAllowed(T type, String property);
}

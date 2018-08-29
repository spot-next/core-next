package io.spotnext.commerce.service;

import io.spotnext.core.infrastructure.service.UserService;
import io.spotnext.itemtype.commerce.customer.Customer;
import io.spotnext.itemtype.core.user.UserGroup;

/**
 * This service provides customer-oriented functionality, like password reset.
 *
 */
public interface CustomerService extends UserService<Customer, UserGroup> {

	/**
	 * Creates a password reset token for the given user. The token is only
	 * valid for a certain amount of time.
	 *
	 * @param customer a {@link io.spotnext.itemtype.commerce.customer.Customer} object.
	 * @return a {@link java.lang.String} object.
	 */
	String createResetPasswordToken(Customer customer);

}

package io.spotnext.commerce.service;

import io.spotnext.itemtype.commerce.customer.Customer;

/**
 * This service provides customer-oriented functionality, like password reset.
 *
 */
public interface CustomerService {

	/**
	 * Creates a password reset token for the given user. The token is only
	 * valid for a certain amount of time.
	 *
	 * @param customer a {@link Customer} object.
	 * @return a {@link java.lang.String} object.
	 */
	String createResetPasswordToken(Customer customer);

}

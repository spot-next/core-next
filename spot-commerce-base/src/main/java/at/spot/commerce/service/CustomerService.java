package at.spot.commerce.service;

import at.spot.commerce.model.customer.Customer;

public interface CustomerService {

	/**
	 * Creates a password reset token for the given user. The token is only
	 * valid for a certain amount of time.
	 * 
	 * @param customer
	 * @return
	 */
	String createResetPasswordToken(Customer customer);
}

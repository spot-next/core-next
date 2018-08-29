package io.spotnext.spring.web.dto;

import org.hibernate.validator.constraints.Email;

/**
 * <p>UserDetails class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class UserDetails {
	@Email
	private String email;
	private String firstName;
	private String lastName;
	private String password;
	private String oldPassword;

	/**
	 * <p>Getter for the field <code>password</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * <p>Setter for the field <code>password</code>.</p>
	 *
	 * @param password a {@link java.lang.String} object.
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * <p>Getter for the field <code>email</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * <p>Setter for the field <code>email</code>.</p>
	 *
	 * @param email a {@link java.lang.String} object.
	 */
	public void setEmail(final String email) {
		this.email = email;
	}

	/**
	 * <p>Getter for the field <code>firstName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * <p>Setter for the field <code>firstName</code>.</p>
	 *
	 * @param firstName a {@link java.lang.String} object.
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	/**
	 * <p>Getter for the field <code>lastName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * <p>Setter for the field <code>lastName</code>.</p>
	 *
	 * @param lastName a {@link java.lang.String} object.
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	/**
	 * <p>Getter for the field <code>oldPassword</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getOldPassword() {
		return oldPassword;
	}

	/**
	 * <p>Setter for the field <code>oldPassword</code>.</p>
	 *
	 * @param oldPassword a {@link java.lang.String} object.
	 */
	public void setOldPassword(final String oldPassword) {
		this.oldPassword = oldPassword;
	}
}

package io.spotnext.core.infrastructure.http;

import com.google.gson.annotations.Expose;

/**
 * <p>Status class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class Status {
	@Expose
	protected String code;
	@Expose
	protected String message;

	/**
	 * <p>Constructor for Status.</p>
	 *
	 * @param code a {@link java.lang.String} object.
	 * @param message a {@link java.lang.String} object.
	 */
	public Status(final String code, final String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * <p>Getter for the field <code>code</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getCode() {
		return code;
	}

	/**
	 * <p>Setter for the field <code>code</code>.</p>
	 *
	 * @param code a {@link java.lang.String} object.
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * <p>Getter for the field <code>message</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * <p>Setter for the field <code>message</code>.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public void setMessage(final String message) {
		this.message = message;
	}
}

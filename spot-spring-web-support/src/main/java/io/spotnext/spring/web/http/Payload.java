package io.spotnext.spring.web.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * <p>Payload class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@JsonInclude(value = Include.NON_NULL)
public class Payload<T> {
	final protected List<Status> errors = new ArrayList<>();
	final protected List<Status> warnings = new ArrayList<>();
	protected T data;

	/**
	 * <p>of.</p>
	 *
	 * @param data a T object.
	 * @param <T> a T object.
	 * @return a {@link io.spotnext.spring.web.http.Payload} object.
	 */
	public static <T> Payload<T> of(final T data) {
		return new Payload<>(data);
	}

	/**
	 * <p>of.</p>
	 *
	 * @param data a {@link java.util.Optional} object.
	 * @param <T> a T object.
	 * @return a {@link io.spotnext.spring.web.http.Payload} object.
	 */
	public static <T> Payload<T> of(final Optional<T> data) {
		return new Payload<>(data.orElse(null));
	}

	/**
	 * <p>empty.</p>
	 *
	 * @param <T> a T object.
	 * @return a {@link io.spotnext.spring.web.http.Payload} object.
	 */
	public static <T> Payload<T> empty() {
		return new Payload<>();
	}

	/**
	 * <p>Constructor for Payload.</p>
	 */
	public Payload() {
		// no data
	}

	/**
	 * <p>Constructor for Payload.</p>
	 *
	 * @param data a T object.
	 */
	public Payload(final T data) {
		this.data = data;
	}

	/**
	 * <p>Getter for the field <code>data</code>.</p>
	 *
	 * @return a T object.
	 */
	public T getData() {
		return data;
	}

	/**
	 * <p>Setter for the field <code>data</code>.</p>
	 *
	 * @param data a T object.
	 */
	public void setData(final T data) {
		this.data = data;
	}

	/**
	 * <p>Getter for the field <code>errors</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<Status> getErrors() {
		return errors;
	}

	/**
	 * <p>Getter for the field <code>warnings</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<Status> getWarnings() {
		return warnings;
	}

	/**
	 * <p>addError.</p>
	 *
	 * @param error a {@link io.spotnext.spring.web.http.Status} object.
	 */
	public void addError(final Status error) {
		this.errors.add(error);
	}

	/**
	 * <p>addWarning.</p>
	 *
	 * @param warning a {@link io.spotnext.spring.web.http.Status} object.
	 */
	public void addWarning(final Status warning) {
		this.warnings.add(warning);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return Objects.hashCode(this);
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object obj) {
		return Objects.equals(this, obj);
	}
}

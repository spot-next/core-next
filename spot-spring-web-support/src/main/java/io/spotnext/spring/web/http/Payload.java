package io.spotnext.spring.web.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gson.annotations.Expose;

@JsonInclude(value = Include.NON_NULL)
public class Payload<T> {
	@Expose
	final protected List<Status> errors = new ArrayList<>();
	@Expose
	final protected List<Status> warnings = new ArrayList<>();
	@Expose
	protected T data;

	public static <T> Payload<T> of(final T data) {
		return new Payload<>(data);
	}

	public static <T> Payload<T> of(final Optional<T> data) {
		return new Payload<>(data.orElse(null));
	}

	public static <T> Payload<T> empty() {
		return new Payload<>();
	}

	public Payload() {
		// no data
	}

	public Payload(final T data) {
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public void setData(final T data) {
		this.data = data;
	}

	public List<Status> getErrors() {
		return errors;
	}

	public List<Status> getWarnings() {
		return warnings;
	}

	public void addError(final Status error) {
		this.errors.add(error);
	}

	public void addWarning(final Status warning) {
		this.warnings.add(warning);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return Objects.equals(this, obj);
	}
}

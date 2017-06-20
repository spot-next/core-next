package at.spot.spring.web.dto;

import java.util.ArrayList;
import java.util.List;

public class Response<T> {
	protected T payload;
	final protected List<Status> errors = new ArrayList<>();
	final protected List<Status> warnings = new ArrayList<>();;

	public T getPayload() {
		return payload;
	}

	public void setPayload(final T payload) {
		this.payload = payload;
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
		this.errors.add(warning);
	}
}

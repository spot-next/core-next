package io.spotnext.core.persistence;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import io.spotnext.infrastructure.type.Enumeration;

public enum TypeSystemAction implements Enumeration {
	INIT("init"), UPDATE("update"), CLEAR("clear"), VALIDATE("validate");

	private String internalId;

	private TypeSystemAction(String internalId) {
		this.internalId = internalId;
	}

	@Override
	public String getInternalName() {
		return internalId;
	}

	public static Optional<TypeSystemAction> forValue(String value) {
		if (StringUtils.isNotBlank(value)) {
			return Stream.of(values()).filter(v -> value.equals(v.internalId)).findFirst();
		}

		return Optional.empty();
	}

	@Override
	public List<Enumeration> getValues() {
		return Arrays.asList(values());
	}
}

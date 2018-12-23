package io.spotnext.infrastructure.type;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Objects;

import io.spotnext.support.util.ClassUtil;

public class Bean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public int hashCode() {
		Object[] fieldValues = ClassUtil.getAllFields(this.getClass()).stream().map(f -> getFieldValue(f)).toArray();

		return Objects.hash(fieldValues);
	}

	protected Object getFieldValue(Field field) {
		try {
			return field.get(this);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException(String.format("Could not read field %s of type %s", field.getName(), this.getClass().getName()), e);
		}
	}

	@Override
	public boolean equals(Object obj) {
		return Objects.equals(this, obj);
	}
}

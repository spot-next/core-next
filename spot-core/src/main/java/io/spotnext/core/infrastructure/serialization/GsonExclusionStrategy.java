package io.spotnext.core.infrastructure.serialization;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * If the strategy is created with skipFieldsAnnotatedWithIgnore = true and the
 * given object's field has the {@link com.fasterxml.jackson.annotation.JsonIgnore} annotation, it will be
 * skipped during serialization.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class GsonExclusionStrategy implements ExclusionStrategy {

	protected boolean skipFieldsAnnotatedWithIgnore;

	/**
	 * <p>Constructor for GsonExclusionStrategy.</p>
	 *
	 * @param skipFieldsAnnotatedWithExpose a boolean.
	 */
	public GsonExclusionStrategy(final boolean skipFieldsAnnotatedWithExpose) {
		this.skipFieldsAnnotatedWithIgnore = skipFieldsAnnotatedWithExpose;
	}

	/** {@inheritDoc} */
	@Override
	public boolean shouldSkipField(final FieldAttributes f) {
		if (skipFieldsAnnotatedWithIgnore && f.getAnnotation(JsonIgnore.class) != null) {
			return true;
		}

		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean shouldSkipClass(final Class<?> clazz) {
		return false;
	}
}

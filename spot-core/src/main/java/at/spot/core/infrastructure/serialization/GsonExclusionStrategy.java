package at.spot.core.infrastructure.serialization;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * If the strategy is created with skipFieldsAnnotatedWithIgnore = true and the
 * given object's field has the {@link JsonIgnore} annotation, it will be
 * skipped during serialization.
 */
public class GsonExclusionStrategy implements ExclusionStrategy {

	protected boolean skipFieldsAnnotatedWithIgnore;

	public GsonExclusionStrategy(final boolean skipFieldsAnnotatedWithExpose) {
		this.skipFieldsAnnotatedWithIgnore = skipFieldsAnnotatedWithExpose;
	}

	@Override
	public boolean shouldSkipField(final FieldAttributes f) {
		if (skipFieldsAnnotatedWithIgnore && f.getAnnotation(JsonIgnore.class) != null) {
			return true;
		}

		return false;
	}

	@Override
	public boolean shouldSkipClass(final Class<?> clazz) {
		return false;
	}
}
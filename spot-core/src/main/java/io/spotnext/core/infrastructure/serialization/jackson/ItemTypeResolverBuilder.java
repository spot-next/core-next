package io.spotnext.core.infrastructure.serialization.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

import io.spotnext.infrastructure.type.Item;

/**
 * <p>ItemTypeResolverBuilder class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ItemTypeResolverBuilder extends DefaultTypeResolverBuilder {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for ItemTypeResolverBuilder.</p>
	 */
	public ItemTypeResolverBuilder() {
		super(DefaultTyping.NON_FINAL);
	}

	/** {@inheritDoc} */
	@Override
	public boolean useForType(JavaType t) {
		if (Item.class.isAssignableFrom(t.getRawClass())) {
			return true;
		}

		return false;
	}
}

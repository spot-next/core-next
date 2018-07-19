package at.spot.core.infrastructure.serialization.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

import at.spot.core.types.Item;

public class ItemTypeResolverBuilder extends DefaultTypeResolverBuilder {
	private static final long serialVersionUID = 1L;

	public ItemTypeResolverBuilder() {
		super(DefaultTyping.NON_FINAL);
	}

	@Override
	public boolean useForType(JavaType t) {
		if (Item.class.isAssignableFrom(t.getRawClass())) {
			return true;
		}

		return false;
	}
}
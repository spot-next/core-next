package at.spot.core.persistence.valueprovider.impl;

import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.spot.core.data.model.Item;
import at.spot.core.persistence.valueprovider.ItemPropertyValueProvider;

@Component
@Scope("prototype")
public class CollectionValueProvider implements ItemPropertyValueProvider {

	@Override
	public <T, I extends Item> T readValue(I item, String attributeName) throws NotReadablePropertyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T, I extends Item> void writeValue(I item, String attributeName, T value)
			throws NotWritablePropertyException {
		// TODO Auto-generated method stub

	}

}

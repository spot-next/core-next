package io.spotnext.core.infrastructure.support;

import java.lang.reflect.Field;
import java.util.Collection;

import io.spotnext.core.infrastructure.annotation.Relation;
import io.spotnext.core.support.util.ClassUtil;
import io.spotnext.core.types.Item;

public class ItemCollectionFactory {

	public static <I extends Item> ProxyCollection<I> wrap(Item owner, String propertyName,
			Collection<I> collectionToWrap) {

		final Field properyField = ClassUtil.getFieldDefinition(owner.getClass(), propertyName, true);
		final ProxyCollection<?> proxyCol = new ProxyCollection<I>(collectionToWrap,
				(e) -> updateOwnerReference(properyField, owner, e),
				(e) -> updateOwnerReference(properyField, null, e));

		return (ProxyCollection<I>) proxyCol;
	}

	private static void updateOwnerReference(final Field collectionPropertyField, final Item owner,
			final Item ownedItem) {
		final Relation relation = ClassUtil.getAnnotation(collectionPropertyField, Relation.class);

		final String mappedTo = relation.mappedTo();
		ClassUtil.setField(ownedItem, mappedTo, owner);
	}
}

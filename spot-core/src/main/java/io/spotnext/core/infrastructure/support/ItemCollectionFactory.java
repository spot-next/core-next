package io.spotnext.core.infrastructure.support;

import java.lang.reflect.Field;
import java.util.Collection;

import io.spotnext.core.infrastructure.annotation.Relation;
import io.spotnext.core.support.util.ClassUtil;
import io.spotnext.core.types.Item;

/**
 * <p>ItemCollectionFactory class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ItemCollectionFactory {

	/**
	 * <p>wrap.</p>
	 *
	 * @param owner a {@link io.spotnext.core.types.Item} object.
	 * @param propertyName a {@link java.lang.String} object.
	 * @param collectionToWrap a {@link java.util.Collection} object.
	 * @param <I> a I object.
	 * @return a {@link io.spotnext.core.infrastructure.support.ProxyCollection} object.
	 */
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

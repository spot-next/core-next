package at.spot.core.infrastructure.support;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.support.util.ClassUtil;
import at.spot.core.types.Item;

public class ItemCollectionFactory {
	private static final Map<Integer, ProxyCollection<?>> proxyCollectionCache = new HashMap<>();

	public static <I extends Item> ProxyCollection<I> wrap(Item owner, String propertyName,
			Collection<I> collectionToWrap) {

		Field properyField = ClassUtil.getFieldDefinition(owner.getClass(), propertyName, true);
		ProxyCollection<?> proxyCol = proxyCollectionCache.get(collectionToWrap.hashCode());

		if (proxyCol == null) {
			proxyCol = new ProxyCollection<I>(collectionToWrap, (e) -> updateOwnerReference(properyField, owner, e),
					(e) -> updateOwnerReference(properyField, null, e));
			proxyCollectionCache.put(collectionToWrap.hashCode(), proxyCol);
		}

		return (ProxyCollection<I>) proxyCol;
	}

	private static void updateOwnerReference(final Field collectionPropertyField, final Item owner,
			final Item ownedItem) {
		final Relation relation = ClassUtil.getAnnotation(collectionPropertyField, Relation.class);

		final String mappedTo = relation.mappedTo();
		ClassUtil.setField(ownedItem, mappedTo, owner);
	}
}

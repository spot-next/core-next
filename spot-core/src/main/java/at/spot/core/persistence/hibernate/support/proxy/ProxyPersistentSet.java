package at.spot.core.persistence.hibernate.support.proxy;

import java.util.function.Consumer;

import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.collection.spi.PersistentCollection;

public class ProxyPersistentSet<E> extends ProxySet<E> implements ProxyPersistentCollection {
	private static final long serialVersionUID = 1L;

	public ProxyPersistentSet(final PersistentSet proxiedColletion, final Consumer<E> beforeAdd,
			final Consumer<E> beforeRemove) {

		super(proxiedColletion, beforeAdd, beforeRemove);
	}

	@Override
	public PersistentCollection getProxiedColletion() {
		return (PersistentCollection) proxiedCollection;
	}

}

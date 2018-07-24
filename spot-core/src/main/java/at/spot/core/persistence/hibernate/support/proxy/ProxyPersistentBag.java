package at.spot.core.persistence.hibernate.support.proxy;

import java.util.function.Consumer;

import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.spi.PersistentCollection;

public class ProxyPersistentBag<E> extends ProxyList<E> implements ProxyPersistentCollection {
	private static final long serialVersionUID = 1L;

	public ProxyPersistentBag(final PersistentBag proxiedColletion, final Consumer<E> beforeAdd,
			final Consumer<E> beforeRemove) {

		super(proxiedColletion, beforeAdd, beforeRemove);
	}

	@Override
	public PersistentCollection getProxiedColletion() {
		return (PersistentCollection) proxiedCollection;
	}

}

package at.spot.core.persistence.hibernate.support.proxy;

import java.util.function.Consumer;

import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.spi.PersistentCollection;

public class ProxyPersistentList<E> extends ProxyList<E> implements ProxyPersistentCollection {
	private static final long serialVersionUID = 1L;

	public ProxyPersistentList(final PersistentList proxiedColletion, final Consumer<E> beforeAdd,
			final Consumer<E> beforeRemove) {

		super(proxiedColletion, beforeAdd, beforeRemove);
	}

	@Override
	public PersistentCollection getProxiedColletion() {
		return (PersistentCollection) proxiedCollection;
	}

}

package at.spot.core.persistence.hibernate.support;

import java.util.Set;

import org.hibernate.collection.internal.PersistentSet;

public class ProxySet<E, P extends PersistentSet> extends ProxyCollection<E, P> implements Set<E> {
	private static final long serialVersionUID = 1L;

	public ProxySet(final P proxiedColletion) {
		super(proxiedColletion);
	}

}

package at.spot.core.persistence.hibernate.support.proxy;

import java.util.Set;
import java.util.function.Consumer;

public class ProxySet<E> extends ProxyCollection<E, Set<E>> implements Set<E> {
	private static final long serialVersionUID = 1L;

	public ProxySet(final Set<E> proxiedColletion, final Consumer<E> beforeAdd, final Consumer<E> beforeRemove) {
		super(proxiedColletion, beforeAdd, beforeRemove);
	}

}

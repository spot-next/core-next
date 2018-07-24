package at.spot.core.persistence.hibernate.support.proxy;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

public abstract class ProxyCollection<E, C extends Collection<E>> implements Collection<E>, Serializable {
	private static final long serialVersionUID = 1L;

	protected C proxiedCollection;

	protected Consumer<E> beforeAdd;
	protected Consumer<E> beforeRemove;

	/**
	 * @param proxiedColletion
	 *            the actual collection implementation
	 * @param owner
	 *            the Entity that "owns" (=contains) the relation
	 * @param role
	 *            the role the collection has (= what owner property it is bound
	 *            to)
	 */
	public ProxyCollection(final C proxiedColletion, final Consumer<E> beforeAdd, final Consumer<E> beforeRemove) {
		this.proxiedCollection = proxiedColletion;
		this.beforeAdd = beforeAdd;
		this.beforeRemove = beforeRemove;
	}

	public C getProxiedCollection() {
		return proxiedCollection;
	}

	@Override
	public int size() {
		return proxiedCollection.size();
	}

	@Override
	public boolean isEmpty() {
		return proxiedCollection.isEmpty();
	}

	@Override
	public boolean contains(final Object o) {
		return proxiedCollection.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return proxiedCollection.iterator();
	}

	@Override
	public Object[] toArray() {
		return proxiedCollection.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		return proxiedCollection.toArray(a);
	}

	@Override
	public boolean add(final E e) {
		return proxiedCollection.add(e);
	}

	@Override
	public boolean remove(final Object o) {
		return proxiedCollection.remove(o);
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return proxiedCollection.containsAll(c);
	}

	@Override
	public boolean addAll(final Collection<? extends E> c) {
		return proxiedCollection.addAll(c);
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		return proxiedCollection.removeAll(c);
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		return proxiedCollection.retainAll(c);
	}

	@Override
	public void clear() {
		proxiedCollection.clear();
	}

}

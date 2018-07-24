package at.spot.core.persistence.hibernate.support.proxy;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

public class ProxyList<E> extends ProxyCollection<E, List<E>> implements List<E> {
	private static final long serialVersionUID = 1L;

	public ProxyList(final List<E> proxiedColletion, final Consumer<E> beforeAdd, final Consumer<E> beforeRemove) {
		super(proxiedColletion, beforeAdd, beforeRemove);
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends E> c) {
		return proxiedCollection.addAll(index, c);
	}

	@Override
	public E get(final int index) {
		return proxiedCollection.get(index);
	}

	@Override
	public E set(final int index, final E element) {
		return proxiedCollection.set(index, element);
	}

	@Override
	public void add(final int index, final E element) {
		proxiedCollection.add(index, element);
	}

	@Override
	public E remove(final int index) {
		return proxiedCollection.remove(index);
	}

	@Override
	public int indexOf(final Object o) {
		return proxiedCollection.indexOf(o);
	}

	@Override
	public int lastIndexOf(final Object o) {
		return proxiedCollection.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return proxiedCollection.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(final int index) {
		return proxiedCollection.listIterator(index);
	}

	@Override
	public List<E> subList(final int fromIndex, final int toIndex) {
		return proxiedCollection.subList(fromIndex, toIndex);
	}

}

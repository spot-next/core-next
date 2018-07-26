package at.spot.core.infrastructure.support;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;

public class ProxyCollection<E> implements List<E>, Set<E> {
	private static final long serialVersionUID = 1L;

	private Collection<E> proxiedCollection;
	private Consumer<E> beforeAdd;
	private Consumer<E> beforeRemove;

	public ProxyCollection(final Collection<E> proxiedColletion, final Consumer<E> beforeAdd,
			final Consumer<E> beforeRemove) {
		this.proxiedCollection = proxiedColletion;
		this.beforeAdd = beforeAdd;
		this.beforeRemove = beforeRemove;
	}

	public Consumer<E> getBeforeAdd() {
		return beforeAdd;
	}

	public void setBeforeAdd(Consumer<E> beforeAdd) {
		this.beforeAdd = beforeAdd;
	}

	public Consumer<E> getBeforeRemove() {
		return beforeRemove;
	}

	public void setBeforeRemove(Consumer<E> beforeRemove) {
		this.beforeRemove = beforeRemove;
	}

	public void setProxiedCollection(Collection<E> proxiedCollection) {
		this.proxiedCollection = proxiedCollection;
	}

	public Collection<E> getProxiedCollection() {
		return proxiedCollection;
	}

	@Override
	public boolean add(final E e) {
		if (beforeAdd != null) {
			beforeAdd.accept(e);
		}

		return proxiedCollection.add(e);
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends E> c) {
		if (beforeAdd != null) {
			for (E e : c) {
				beforeAdd.accept(e);
			}
		}

		return ((List<E>) proxiedCollection).addAll(index, c);
	}

	@Override
	public boolean addAll(final Collection<? extends E> c) {
		if (beforeAdd != null) {
			for (E e : c) {
				beforeAdd.accept(e);
			}
		}

		return proxiedCollection.addAll(c);
	}

	@Override
	public E set(final int index, final E element) {
		if (beforeRemove != null) {
			beforeRemove.accept(get(index));
		}

		if (element != null) {
			if (beforeAdd != null) {
				beforeAdd.accept(element);
			}
		}

		return ((List<E>) proxiedCollection).set(index, element);
	}

	@Override
	public boolean remove(final Object o) {
		if (beforeRemove != null) {
			beforeRemove.accept((E) o);
		}

		return proxiedCollection.remove(o);
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		if (beforeRemove != null) {
			for (Object e : c) {
				beforeRemove.accept((E) e);
			}
		}

		return proxiedCollection.removeAll(c);
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		return ((List<E>) proxiedCollection).retainAll(c);
	}

	@Override
	public void clear() {
		if (beforeRemove != null) {
			for (E e : this) {
				beforeRemove.accept(e);
			}
		}

		proxiedCollection.clear();
	}

	@Override
	public void add(final int index, final E element) {
		if (beforeAdd != null) {
			beforeAdd.accept(element);
		}

		((List<E>) proxiedCollection).add(index, element);
	}

	@Override
	public E remove(final int index) {
		if (beforeRemove != null) {
			beforeRemove.accept(get(index));
		}

		return ((List<E>) proxiedCollection).remove(index);
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
	public boolean containsAll(final Collection<?> c) {
		return proxiedCollection.containsAll(c);
	}

	@Override
	public E get(final int index) {
		return ((List<E>) proxiedCollection).get(index);
	}

	@Override
	public int indexOf(final Object o) {
		return ((List<E>) proxiedCollection).indexOf(o);
	}

	@Override
	public int lastIndexOf(final Object o) {
		return ((List<E>) proxiedCollection).lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return ((List<E>) proxiedCollection).listIterator();
	}

	@Override
	public ListIterator<E> listIterator(final int index) {
		return ((List<E>) proxiedCollection).listIterator(index);
	}

	@Override
	public List<E> subList(final int fromIndex, final int toIndex) {
		return ((List<E>) proxiedCollection).subList(fromIndex, toIndex);
	}

	@Override
	public Spliterator<E> spliterator() {
		return proxiedCollection.spliterator();
	}

}

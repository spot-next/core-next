package io.spotnext.infrastructure.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;

import io.spotnext.support.util.ClassUtil;

/**
 * <p>
 * ProxyCollection class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ProxyCollection<E> implements List<E>, Set<E> {
	private static final long serialVersionUID = 1L;

	private Collection<E> proxiedCollection;
	private transient Consumer<E> beforeAdd;
	private transient Consumer<E> beforeRemove;

	/**
	 * <p>
	 * Constructor for ProxyCollection.
	 * </p>
	 *
	 * @param proxiedCollection a {@link java.util.Collection} object.
	 * @param beforeAdd         a {@link java.util.function.Consumer} object.
	 * @param beforeRemove      a {@link java.util.function.Consumer} object.
	 */
	public ProxyCollection(final Collection<E> proxiedCollection, Class<? extends Collection> collecionType, final Consumer<E> beforeAdd,
			final Consumer<E> beforeRemove) {

		if (proxiedCollection != null) {
			this.proxiedCollection = proxiedCollection;
		} else {
			Class<?> colType = Collection.class;

			// this is just a fallback, it could lead to unwanted side effects
			if (Set.class.equals(collecionType)) {
				colType = HashSet.class;
			} else {
				colType = ArrayList.class;
			}

			this.proxiedCollection = (Collection<E>) ClassUtil.instantiate(colType).get();
		}

		this.beforeAdd = beforeAdd;
		this.beforeRemove = beforeRemove;
	}

	/**
	 * <p>
	 * Getter for the field <code>beforeAdd</code>.
	 * </p>
	 *
	 * @return a {@link java.util.function.Consumer} object.
	 */
	public Consumer<E> getBeforeAdd() {
		return beforeAdd;
	}

	/**
	 * <p>
	 * Setter for the field <code>beforeAdd</code>.
	 * </p>
	 *
	 * @param beforeAdd a {@link java.util.function.Consumer} object.
	 */
	public void setBeforeAdd(Consumer<E> beforeAdd) {
		this.beforeAdd = beforeAdd;
	}

	/**
	 * <p>
	 * Getter for the field <code>beforeRemove</code>.
	 * </p>
	 *
	 * @return a {@link java.util.function.Consumer} object.
	 */
	public Consumer<E> getBeforeRemove() {
		return beforeRemove;
	}

	/**
	 * <p>
	 * Setter for the field <code>beforeRemove</code>.
	 * </p>
	 *
	 * @param beforeRemove a {@link java.util.function.Consumer} object.
	 */
	public void setBeforeRemove(Consumer<E> beforeRemove) {
		this.beforeRemove = beforeRemove;
	}

	/**
	 * <p>
	 * Setter for the field <code>proxiedCollection</code>.
	 * </p>
	 *
	 * @param proxiedCollection a {@link java.util.Collection} object.
	 */
	public void setProxiedCollection(Collection<E> proxiedCollection) {
		this.proxiedCollection = proxiedCollection;
	}

	/**
	 * <p>
	 * Getter for the field <code>proxiedCollection</code>.
	 * </p>
	 *
	 * @return a {@link java.util.Collection} object.
	 */
	public Collection<E> getProxiedCollection() {
		return proxiedCollection;
	}

	/** {@inheritDoc} */
	@Override
	public boolean add(final E e) {
		if (beforeAdd != null) {
			beforeAdd.accept(e);
		}

		return proxiedCollection.add(e);
	}

	/** {@inheritDoc} */
	@Override
	public boolean addAll(final int index, final Collection<? extends E> c) {
		if (proxiedCollection instanceof List) {
			if (beforeAdd != null) {
				for (E e : c) {
					beforeAdd.accept(e);
				}
			}

			return ((List<E>) proxiedCollection).addAll(index, c);
		}

		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean addAll(final Collection<? extends E> c) {
		if (c != null) {
			if (beforeAdd != null) {
				for (E e : c) {
					beforeAdd.accept(e);
				}
			}

			return proxiedCollection.addAll(c);
		}

		return false;
	}

	/** {@inheritDoc} */
	@Override
	public E set(final int index, final E element) {
		if (proxiedCollection instanceof List) {
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

		return null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean remove(final Object o) {
		if (beforeRemove != null) {
			beforeRemove.accept((E) o);
		}

		return proxiedCollection.remove(o);
	}

	/** {@inheritDoc} */
	@Override
	public boolean removeAll(final Collection<?> c) {
		if (beforeRemove != null) {
			for (Object e : c) {
				beforeRemove.accept((E) e);
			}
		}

		return proxiedCollection.removeAll(c);
	}

	/** {@inheritDoc} */
	@Override
	public boolean retainAll(final Collection<?> c) {
		if (proxiedCollection instanceof List) {
			return ((List<E>) proxiedCollection).retainAll(c);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void clear() {
		if (proxiedCollection != null) {
			if (beforeRemove != null) {
				for (E e : proxiedCollection) {
					beforeRemove.accept(e);
				}
			}

			proxiedCollection.clear();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void add(final int index, final E element) {
		if (beforeAdd != null) {
			beforeAdd.accept(element);
		}

		if (proxiedCollection instanceof List) {
			((List<E>) proxiedCollection).add(index, element);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/** {@inheritDoc} */
	@Override
	public E remove(final int index) {
		if (beforeRemove != null) {
			beforeRemove.accept(get(index));
		}

		if (proxiedCollection instanceof List) {
			return ((List<E>) proxiedCollection).remove(index);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return proxiedCollection.size();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return proxiedCollection.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(final Object o) {
		return proxiedCollection.contains(o);
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<E> iterator() {
		return proxiedCollection.iterator();
	}

	/** {@inheritDoc} */
	@Override
	public Object[] toArray() {
		return proxiedCollection.toArray();
	}

	/** {@inheritDoc} */
	@Override
	public <T> T[] toArray(final T[] a) {
		return proxiedCollection.toArray(a);
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsAll(final Collection<?> c) {
		return proxiedCollection.containsAll(c);
	}

	/** {@inheritDoc} */
	@Override
	public E get(final int index) {
		if (proxiedCollection instanceof List) {
			return ((List<E>) proxiedCollection).get(index);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/** {@inheritDoc} */
	@Override
	public int indexOf(final Object o) {
		if (proxiedCollection instanceof List) {
			return ((List<E>) proxiedCollection).indexOf(o);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/** {@inheritDoc} */
	@Override
	public int lastIndexOf(final Object o) {
		if (proxiedCollection instanceof List) {
			return ((List<E>) proxiedCollection).lastIndexOf(o);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/** {@inheritDoc} */
	@Override
	public ListIterator<E> listIterator() {
		if (proxiedCollection instanceof List) {
			return ((List<E>) proxiedCollection).listIterator();
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/** {@inheritDoc} */
	@Override
	public ListIterator<E> listIterator(final int index) {
		if (proxiedCollection instanceof List) {
			return ((List<E>) proxiedCollection).listIterator(index);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/** {@inheritDoc} */
	@Override
	public List<E> subList(final int fromIndex, final int toIndex) {
		if (proxiedCollection instanceof List) {
			return ((List<E>) proxiedCollection).subList(fromIndex, toIndex);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/** {@inheritDoc} */
	@Override
	public Spliterator<E> spliterator() {
		return proxiedCollection.spliterator();
	}

}

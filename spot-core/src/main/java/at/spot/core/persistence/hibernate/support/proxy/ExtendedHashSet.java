package at.spot.core.persistence.hibernate.support.proxy;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

public class ExtendedHashSet<E> extends HashSet<E> {
	private static final long serialVersionUID = 1L;

	protected Consumer<E> beforeAdd;
	protected Consumer<E> beforeRemove;

	public ExtendedHashSet(final Consumer<E> beforeAdd, final Consumer<E> beforeRemove) {
		super();
		this.beforeAdd = beforeAdd;
		this.beforeRemove = beforeRemove;
	}

	public ExtendedHashSet(Collection<E> collection, final Consumer<E> beforeAdd, final Consumer<E> beforeRemove) {
		super(collection);
		this.beforeAdd = beforeAdd;
		this.beforeRemove = beforeRemove;
	}

	public ExtendedHashSet(Collection<E> collection) {
		super(collection);
	}

	public ExtendedHashSet() {
		super();
	}

	@Override
	public boolean add(final E e) {
		if (beforeAdd != null) {
			beforeAdd.accept(e);
		}

		return super.add(e);
	}

	@Override
	public boolean addAll(final Collection<? extends E> c) {
		if (beforeAdd != null) {
			for (E e : c) {
				beforeAdd.accept(e);
			}
		}

		return super.addAll(c);
	}

	@Override
	public boolean remove(final Object o) {
		if (beforeRemove != null) {
			beforeRemove.accept((E) o);
		}

		return super.remove(o);
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		if (beforeRemove != null) {
			for (Object e : c) {
				beforeRemove.accept((E) e);
			}
		}

		return super.removeAll(c);
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		return super.retainAll(c);
	}

	@Override
	public void clear() {
		if (beforeRemove != null) {
			for (E e : this) {
				beforeRemove.accept(e);
			}
		}

		super.clear();
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

}

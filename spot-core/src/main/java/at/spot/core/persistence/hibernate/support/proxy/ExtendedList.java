package at.spot.core.persistence.hibernate.support.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class ExtendedList<E> extends ArrayList<E> {
	private static final long serialVersionUID = 1L;

	protected Consumer<E> beforeAdd;
	protected Consumer<E> beforeRemove;

	public ExtendedList(final Consumer<E> beforeAdd, final Consumer<E> beforeRemove) {
		super();
		this.beforeAdd = beforeAdd;
		this.beforeRemove = beforeRemove;
	}

	public ExtendedList(Collection<E> collection, final Consumer<E> beforeAdd, final Consumer<E> beforeRemove) {
		super(collection);
		this.beforeAdd = beforeAdd;
		this.beforeRemove = beforeRemove;
	}

	public ExtendedList(Collection<E> collection) {
		super(collection);
	}

	public ExtendedList() {
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
	public boolean addAll(final int index, final Collection<? extends E> c) {
		return super.addAll(index, c);
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
	public E set(final int index, final E element) {
		if (beforeRemove != null) {
			beforeRemove.accept(get(index));
		}

		if (element != null) {
			if (beforeAdd != null) {
				beforeAdd.accept(element);
			}
		}

		return super.set(index, element);
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

	@Override
	public void add(final int index, final E element) {
		if (beforeRemove != null) {
			beforeRemove.accept(get(index));
		}

		if (beforeAdd != null) {
			beforeAdd.accept(element);
		}

		super.add(index, element);
	}

	@Override
	public E remove(final int index) {
		if (beforeRemove != null) {
			beforeRemove.accept(get(index));
		}

		return super.remove(index);
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

package at.spot.core.infrastructure.type.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ObservableList<E> implements List<E> {

	List<E> internalList;
	Observer observer;
	String collectionName;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ObservableList(Class<? extends List> type, Observer listener, String collectionName)
			throws RuntimeException {

		try {
			this.internalList = (List<E>) type.newInstance();
		} catch (IllegalAccessException | InstantiationException e) {
			throw new RuntimeException("Cannot instantiate object", e);
		}

		this.observer = listener;
		this.collectionName = collectionName;
	}

	public void notifyObserver(ObservableChange change, Object element) {
		try {
			this.observer.notify(this.collectionName, change, element);
		} catch (Exception e) {
			// if an observer causes an error, we just ignore it
		}
	}

	@Override
	public int size() {
		return internalList.size();
	}

	@Override
	public boolean isEmpty() {
		return internalList.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return internalList.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return internalList.iterator();
	}

	@Override
	public Object[] toArray() {
		return internalList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return internalList.toArray(a);
	}

	@Override
	public boolean add(E e) {
		notifyObserver(ObservableChange.ADD, e);

		return internalList.add(e);
	}

	@Override
	public boolean remove(Object o) {
		notifyObserver(ObservableChange.REMOVE, o);

		return internalList.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return internalList.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		notifyObserver(ObservableChange.ADD, c);

		return internalList.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		notifyObserver(ObservableChange.ADD, c);

		return internalList.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		notifyObserver(ObservableChange.REMOVE, c);

		return internalList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return internalList.retainAll(c);
	}

	@Override
	public void clear() {
		internalList.clear();
	}

	@Override
	public E get(int index) {
		return internalList.get(index);
	}

	@Override
	public E set(int index, E element) {
		notifyObserver(ObservableChange.SET, element);

		return internalList.set(index, element);
	}

	@Override
	public void add(int index, E element) {
		notifyObserver(ObservableChange.ADD, element);

		internalList.add(index, element);
	}

	@Override
	public E remove(int index) {
		notifyObserver(ObservableChange.REMOVE, internalList.get(index));

		return internalList.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return internalList.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return internalList.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return internalList.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return internalList.listIterator(index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return internalList.subList(fromIndex, toIndex);
	}
}

package at.spot.core.persistence.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;

import org.apache.commons.collections4.CollectionUtils;

import at.spot.core.infrastructure.type.ModificationType;

public class ObservableCollection<E> implements List<E>, Set<E> {

	protected final Collection<E> sourceCollection;
	protected BiConsumer<ModificationType, List<E>> listener;

	public ObservableCollection(final Collection<E> sourceCollection,
			final BiConsumer<ModificationType, List<E>> listener) {
		this.sourceCollection = sourceCollection;
		this.listener = listener;
	}

	public ObservableCollection(final Set<E> sourceCollection, final BiConsumer<ModificationType, List<E>> listener) {
		this((Collection<E>) sourceCollection, listener);
	}

	@Override
	public int size() {
		return sourceCollection.size();
	}

	@Override
	public boolean isEmpty() {
		return sourceCollection.isEmpty();
	}

	@Override
	public boolean contains(final Object o) {
		return sourceCollection.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return sourceCollection.iterator();
	}

	@Override
	public Object[] toArray() {
		return sourceCollection.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		return sourceCollection.toArray(a);
	}

	@Override
	public boolean add(final E element) {
		final boolean ret = sourceCollection.add(element);
		listener.accept(ModificationType.ADD, Arrays.asList(element));
		return ret;
	}

	@Override
	public boolean remove(final Object o) {
		final boolean ret = sourceCollection.remove(o);
		listener.accept(ModificationType.REMOVE, Arrays.asList((E) o));
		return ret;
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return sourceCollection.containsAll(c);
	}

	@Override
	public boolean addAll(final Collection<? extends E> c) {
		final boolean ret = sourceCollection.addAll(c);
		listener.accept(ModificationType.ADD, new ArrayList<>(c));
		return ret;
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends E> c) {
		final boolean ret = ((List<E>) sourceCollection).addAll(index, c);
		listener.accept(ModificationType.ADD, new ArrayList<>(c));
		return ret;
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		final boolean ret = sourceCollection.removeAll(c);
		listener.accept(ModificationType.REMOVE, new ArrayList<>((Collection<E>) c));
		return ret;
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		final List<E> itemsToRetain = (List<E>) CollectionUtils.retainAll(sourceCollection, c);
		final List<E> itemsToRemove = (List<E>) CollectionUtils.disjunction(sourceCollection, itemsToRetain);

		listener.accept(ModificationType.REMOVE, itemsToRemove);
		sourceCollection.clear();
		sourceCollection.addAll(itemsToRetain);

		return itemsToRemove.size() > 0;
	}

	@Override
	public void clear() {
		final List<E> removed = new ArrayList<E>(sourceCollection);
		sourceCollection.clear();
		listener.accept(ModificationType.REMOVE, removed);
	}

	@Override
	public E get(final int index) {
		return ((List<E>) sourceCollection).get(index);
	}

	@Override
	public E set(final int index, final E element) {
		final E ret = ((List<E>) sourceCollection).set(index, element);
		listener.accept(ModificationType.ADD, Arrays.asList(element));
		return ret;
	}

	@Override
	public void add(final int index, final E element) {
		((List<E>) sourceCollection).add(index, element);
		listener.accept(ModificationType.ADD, Arrays.asList(element));
	}

	@Override
	public E remove(final int index) {
		final E ret = ((List<E>) sourceCollection).remove(index);
		listener.accept(ModificationType.REMOVE, Arrays.asList(ret));
		return ret;
	}

	@Override
	public int indexOf(final Object o) {
		return ((List<E>) sourceCollection).indexOf(o);
	}

	@Override
	public int lastIndexOf(final Object o) {
		return ((List<E>) sourceCollection).lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return ((List<E>) sourceCollection).listIterator();
	}

	@Override
	public ListIterator<E> listIterator(final int index) {
		return ((List<E>) sourceCollection).listIterator(index);
	}

	@Override
	public List<E> subList(final int fromIndex, final int toIndex) {
		return ((List<E>) sourceCollection).subList(fromIndex, toIndex);
	}

	@Override
	public Spliterator<E> spliterator() {
		return ((Set<E>) this.sourceCollection).spliterator();
	}
}
package at.spot.core.infrastructure.type;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.type.collection.ObservableChange;
import at.spot.core.model.Item;
import at.spot.core.model.OneToManyRelation;

public class OneToManyRelationProxyList<E extends Item> implements List<E> {

	protected ModelService modelService;

	protected List<E> internalList;
	protected Item relationSource;
	protected String relationSourcePropertyName;
	protected Class<E> relationTargetType;
	protected String relationTargetPropertyName;
	protected Class<? extends OneToManyRelation<?, E>> relationType;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public OneToManyRelationProxyList(final Class<? extends List> collectionType, final Item relationSource,
			final String relationSourcePropertyName, final Class<E> relationTargetType,
			final String relationTargetPropertyName, final Class<? extends OneToManyRelation<?, E>> relationType,
			final ModelService modelService) throws RuntimeException {

		try {
			this.internalList = collectionType.newInstance();
		} catch (IllegalAccessException | InstantiationException e) {
			throw new RuntimeException("Cannot instantiate object", e);
		}

		this.relationSource = relationSource;
		this.relationSourcePropertyName = relationSourcePropertyName;
		this.relationTargetType = relationTargetType;
		this.relationTargetPropertyName = relationTargetPropertyName;
		this.relationType = relationType;
		this.modelService = modelService;

		loadEntries();
	}

	protected void loadEntries() {
		final Map<String, Comparable<?>> params = new HashMap<>();
		params.put("source", relationSource.pk);

		final List<? extends OneToManyRelation<?, E>> relItems = modelService.getAll(relationType, params);

		internalList = relItems.stream().map((r) -> {
			return r.target;
		}).collect(Collectors.toList());
	}

	public void notifyObserver(final ObservableChange change, final Object element) {
		try {
			this.relationSource.notify(this.relationSourcePropertyName, change, element);
		} catch (final Exception e) {
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
	public boolean contains(final Object o) {
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
	public <T> T[] toArray(final T[] a) {
		return internalList.toArray(a);
	}

	@Override
	public boolean add(final E e) {
		notifyObserver(ObservableChange.ADD, e);

		return internalList.add(e);
	}

	@Override
	public boolean remove(final Object o) {
		notifyObserver(ObservableChange.REMOVE, o);

		return internalList.remove(o);
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return internalList.containsAll(c);
	}

	@Override
	public boolean addAll(final Collection<? extends E> c) {
		notifyObserver(ObservableChange.ADD, c);

		return internalList.addAll(c);
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends E> c) {
		notifyObserver(ObservableChange.ADD, c);

		return internalList.addAll(index, c);
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		notifyObserver(ObservableChange.REMOVE, c);

		return internalList.removeAll(c);
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		return internalList.retainAll(c);
	}

	@Override
	public void clear() {
		internalList.clear();
	}

	@Override
	public E get(final int index) {
		return internalList.get(index);
	}

	@Override
	public E set(final int index, final E element) {
		notifyObserver(ObservableChange.SET, element);

		return internalList.set(index, element);
	}

	@Override
	public void add(final int index, final E element) {
		notifyObserver(ObservableChange.ADD, element);

		internalList.add(index, element);
	}

	@Override
	public E remove(final int index) {
		notifyObserver(ObservableChange.REMOVE, internalList.get(index));

		return internalList.remove(index);
	}

	@Override
	public int indexOf(final Object o) {
		return internalList.indexOf(o);
	}

	@Override
	public int lastIndexOf(final Object o) {
		return internalList.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return internalList.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(final int index) {
		return internalList.listIterator(index);
	}

	@Override
	public List<E> subList(final int fromIndex, final int toIndex) {
		return internalList.subList(fromIndex, toIndex);
	}
}

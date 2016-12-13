package at.spot.core.infrastructure.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.collections4.CollectionUtils;

import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.spring.support.Registry;
import at.spot.core.model.Item;
import at.spot.core.persistence.query.QueryCondition;
import at.spot.core.persistence.query.QueryResult;
import at.spot.core.persistence.service.QueryService;

public class RelationProxyList<E extends Item> implements List<E> {

	protected boolean isInitialized = false;

	protected final List<E> internalList;
	protected final Relation relationDefinition;
	protected final long referencingItemPk;

	// these referenced items have changed and need to be added, removed or
	// updated when saving
	final protected List<E> itemsToAdd = new ArrayList<>();
	final protected List<E> itemsToRemove = new ArrayList<>();
	final protected List<E> itemsToUpdate = new ArrayList<>();
	final protected Runnable changeCallback;

	public RelationProxyList(final Relation relationDefinition, final long referencingItemPk,
			final String onChangePropertyName, final Runnable changeCallback) throws RuntimeException {

		this.relationDefinition = relationDefinition;
		this.referencingItemPk = referencingItemPk;
		this.internalList = new ArrayList<>();
		this.changeCallback = changeCallback;
	}

	public void refresh() {
		QueryCondition query = null;

		if (relationDefinition.type() == RelationType.OneToMany) {
			query = (i) -> {
				final Item referencedItem = (Item) ((Item) i).getProperty(relationDefinition.mappedTo());

				return referencedItem != null && referencedItem.pk.equals(referencingItemPk);
			};
		}

		// if (relationDefinition.type() == RelationType.ManyToMany) {
		// query = (i) -> {
		// final List<Item> referencedItem = (List<Item>) ((Item)
		// i).getProperty(relationDefinition.mappedTo());
		//
		// final boolean containsReference = referencedItem.stream().filter((e)
		// -> {return });
		//
		// return referencedItem.pk.equals(referencingItem.pk);
		// };
		// }

		if (query != null) {
			final QueryResult result = getQueryService().query(relationDefinition.referencedType(), query);
			internalList.addAll(result.getResult());
		} else {
			internalList.addAll(Collections.emptyList());
		}
	}

	public void initialize() {
		if (!isInitialized) {
			refresh();
			isInitialized = true;
		}
	}

	protected void setItemReference() {
		// re
	}

	protected void unsetItemReference() {
		// re
	}

	public void notifyObserver(final ListModification change, final Object element) {
		changeCallback.run();
	}

	@Override
	public int size() {
		initialize();
		return internalList.size();
	}

	@Override
	public boolean isEmpty() {
		initialize();
		return internalList.isEmpty();
	}

	@Override
	public boolean contains(final Object o) {
		initialize();
		return internalList.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		initialize();
		return internalList.iterator();
	}

	@Override
	public Object[] toArray() {
		initialize();
		return internalList.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		initialize();
		return internalList.toArray(a);
	}

	@Override
	public boolean add(final E e) {
		initialize();

		itemsToAdd.add(e);
		final boolean ret = internalList.add(e);

		notifyObserver(ListModification.ADD, e);

		return ret;
	}

	@Override
	public boolean remove(final Object o) {
		initialize();

		itemsToRemove.add((E) o);
		final boolean ret = internalList.remove(o);
		;

		notifyObserver(ListModification.REMOVE, o);

		return ret;
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		initialize();

		return internalList.containsAll(c);
	}

	@Override
	public boolean addAll(final Collection<? extends E> c) {
		initialize();

		itemsToAdd.addAll(c);
		final boolean ret = internalList.addAll(c);

		notifyObserver(ListModification.ADD, c);

		return ret;
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends E> c) {
		initialize();

		itemsToAdd.addAll(index, c);
		final boolean ret = internalList.addAll(index, c);

		notifyObserver(ListModification.ADD, c);
		return ret;
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		initialize();

		itemsToRemove.addAll((Collection<E>) c);
		final boolean ret = internalList.removeAll(c);

		notifyObserver(ListModification.REMOVE, c);

		return ret;
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		initialize();

		final List<E> itemsToRetain = (List<E>) CollectionUtils.retainAll(internalList, c);
		final List<E> itemsToRemove = (List<E>) CollectionUtils.disjunction(internalList, itemsToRetain);

		notifyObserver(ListModification.REMOVE, itemsToRemove);

		this.itemsToRemove.addAll(itemsToRemove);
		internalList.clear();
		internalList.addAll(itemsToRetain);

		return itemsToRemove != null && itemsToRemove.size() > 0;
	}

	@Override
	public void clear() {
		internalList.clear();
	}

	@Override
	public E get(final int index) {
		initialize();

		return internalList.get(index);
	}

	@Override
	public E set(final int index, final E element) {
		initialize();

		itemsToRemove.add(internalList.get(index));
		itemsToAdd.add(element);

		final E ret = internalList.set(index, element);

		notifyObserver(ListModification.SET, element);

		return ret;
	}

	@Override
	public void add(final int index, final E element) {
		initialize();

		itemsToAdd.add(element);
		internalList.add(index, element);

		notifyObserver(ListModification.ADD, element);
	}

	@Override
	public E remove(final int index) {
		initialize();

		itemsToRemove.add(internalList.get(index));
		final E ret = internalList.remove(index);

		notifyObserver(ListModification.REMOVE, internalList.get(index));

		return ret;
	}

	@Override
	public int indexOf(final Object o) {
		initialize();

		return internalList.indexOf(o);
	}

	@Override
	public int lastIndexOf(final Object o) {
		initialize();

		return internalList.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		initialize();

		return internalList.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(final int index) {
		initialize();

		return internalList.listIterator(index);
	}

	@Override
	public List<E> subList(final int fromIndex, final int toIndex) {
		initialize();

		return internalList.subList(fromIndex, toIndex);
	}

	/*
	 * Helper functions
	 */

	protected ModelService getModelService() {
		return Registry.getApplicationContext().getBean(ModelService.class);
	}

	protected QueryService getQueryService() {
		return Registry.getApplicationContext().getBean(QueryService.class);
	}

	public List<? extends Item> getItemsToAdd() {
		return itemsToAdd;
	}

	public List<? extends Item> getItemsToRemove() {
		return itemsToRemove;
	}

	public List<? extends Item> getItemsToUpdate() {
		return itemsToUpdate;
	}
}

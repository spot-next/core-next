package at.spot.core.infrastructure.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import at.spot.core.persistence.query.QueryCondition;
import at.spot.core.persistence.query.QueryResult;

import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.core.infrastructure.type.ListModification;
import at.spot.core.infrastructure.type.RelationType;
import at.spot.core.model.Item;
import at.spot.core.persistence.service.QueryService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value = { "SE_TRANSIENT_FIELD_NOT_RESTORED", "SE_BAD_FIELD" })
public class RelationProxyList<E extends Item> implements List<E>, RandomAccess, Serializable {

	private static final long serialVersionUID = 1770972929572955365L;

	protected boolean isInitialized = false;

	final protected Relation relationDefinition;
	// protected final long referencingItemPk;
	// protected Class<? extends Item> referencingItemType;
	final protected E referencingItem;

	final protected boolean isMappedPropertyUnique;
	// final protected SerializableRunnable changeCallback;

	// these referenced items have changed and need to be added, removed or
	// updated when saving
	protected transient final List<E> internalList;
	final protected transient List<E> itemsToRemove = new ArrayList<>();
	final protected transient List<E> itemsToUpdate = new ArrayList<>();
	final protected String onChangePropertyName;

	// public RelationProxyList(final Relation relationDefinition, final Class<?
	// extends Item> referencingItemType,
	// final long referencingItemPk, final boolean isMappedPropertyUnique, final
	// String onChangePropertyName,
	// final Runnable changeCallback) throws RuntimeException {
	//
	// this.relationDefinition = relationDefinition;
	// this.referencingItemPk = referencingItemPk;
	// this.referencingItemType = referencingItemType;
	// this.isMappedPropertyUnique = isMappedPropertyUnique;
	// this.internalList = new ArrayList<>();
	// this.changeCallback = changeCallback;
	// }

	public RelationProxyList(final Relation relationDefinition, final E referencingItem,
			final boolean isMappedPropertyUnique, final String onChangePropertyName) throws RuntimeException {

		this.relationDefinition = relationDefinition;
		this.referencingItem = referencingItem;
		// this.referencingItemType = referencingItem.getClass();
		this.isMappedPropertyUnique = isMappedPropertyUnique;
		this.internalList = new ArrayList<>();
		this.onChangePropertyName = onChangePropertyName;
	}

	public void refresh() {
		QueryCondition<E> query = null;

		if (relationDefinition.type() == RelationType.OneToMany) {
			query = (i) -> {
				final Item referencedItem = (Item) getModelService().getPropertyValue(i, relationDefinition.mappedTo());

				try {
					getModelService().loadProxyModel((Item) i);
				} catch (final ModelNotFoundException e) {
					// ignore item as it might have already been deleted
				}

				return referencedItem != null && referencedItem.getPk().equals(referencingItem.getPk());
			};
		}

		if (relationDefinition.type() == RelationType.ManyToMany) {
			query = (i) -> {
				final List<Item> referencedItems = (List<Item>) getModelService().getPropertyValue(i,
						relationDefinition.mappedTo());

				if (referencedItems != null) {
					return referencedItems.stream().filter((e) -> {
						return e.getPk().equals(referencingItem.getPk());
					}).findAny().isPresent();
				} else {
					return false;
				}
			};
		}

		if (query != null) {
			final QueryResult<E> result = getQueryService().query((Class<E>) relationDefinition.referencedType(), query,
					null, -1, -1, false);
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
		referencingItem.markAsDirty(onChangePropertyName);
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
	public boolean add(final E element) {
		return addAll(Collections.singletonList(element));
	}

	@Override
	public boolean remove(final Object o) {
		initialize();

		removeItemRelation((E) o);
		final boolean ret = internalList.remove(o);

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
		final int index = internalList.size() > 0 ? internalList.size() - 1 : 0;

		return addAll(index, c);
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends E> c) {
		initialize();

		setItemRelation(c);
		final boolean ret = internalList.addAll(index, c);

		notifyObserver(ListModification.ADD, c);
		return ret;
	}

	public void addAllInternal(final Collection<? extends E> c) {
		internalList.addAll(c);
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		initialize();

		removeItemRelation((Collection<? extends E>) c);
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

		removeItemRelation(itemsToRemove);
		internalList.clear();
		internalList.addAll(itemsToRetain);

		return itemsToRemove.size() > 0;
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

		removeItemRelation(internalList.get(index));
		setItemRelation(element);

		final E ret = internalList.set(index, element);

		notifyObserver(ListModification.SET, element);

		return ret;
	}

	@Override
	public void add(final int index, final E element) {
		initialize();

		setItemRelation(element);
		internalList.add(index, element);

		notifyObserver(ListModification.ADD, element);
	}

	@Override
	public E remove(final int index) {
		initialize();

		removeItemRelation(internalList.get(index));
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

	/**
	 * @see #removeItemRelation(Collection)
	 */
	protected void removeItemRelation(final E item) {
		removeItemRelation(Arrays.asList(item));
	}

	/**
	 * Removes the given item's relation to the referencing item.
	 * 
	 * @param items
	 */
	protected void removeItemRelation(final Collection<? extends E> items) {
		for (final E item : items) {
			// if cascade on delete is enabled the referenced item is really
			// removed, not just the relation itself
			// if the referenced item's relation property is unique, this is
			// enforced too, otherwise it the referenced item could possibly
			// violate the uniqueness criteria.
			if (relationDefinition.casacadeOnDelete() || relationIsPartOfUniquePropertyConstraint(item)) {
				itemsToRemove.addAll(items);
			} else {
				if (relationDefinition.type() == RelationType.OneToMany) {
					getModelService().setPropertyValue(item, relationDefinition.mappedTo(), null);
				} else {
					final List<Item> relationList = getModelService().getPropertyValue(item,
							relationDefinition.mappedTo(), List.class);

					final List<Item> toRemoveFromRelation = relationList.stream().filter((i) -> {
						return i.getPk().equals(referencingItem.getPk());
					}).collect(Collectors.toList());

					relationList.removeAll(toRemoveFromRelation);
				}
			}
		}
	}

	/**
	 * @see #setItemRelation(Collection)
	 */
	protected void setItemRelation(final E item) {
		setItemRelation(Arrays.asList(item));
	}

	/**
	 * Updates the referenced item's relation property to the actual referencing
	 * item.
	 * 
	 * @param items
	 */
	protected void setItemRelation(final Collection<? extends E> items) {
		for (final E item : items) {
			if (relationDefinition.type() == RelationType.OneToMany) {
				getModelService().setPropertyValue(item, relationDefinition.mappedTo(), referencingItem);
			} else {
				final RelationProxyList<Item> relationList = (RelationProxyList<Item>) getModelService()
						.getPropertyValue(item, relationDefinition.mappedTo(), List.class);

				// final List<Item> toAddToRelation = relationList.stream().filter((i) -> {
				// return i.getPk().equals(this.referencingItem.getPk());
				// }).collect(Collectors.toList());

				E proxyReferencingItem = getModelService().createProxyModel(referencingItem);

				relationList.addAllInternal(Collections.singletonList(proxyReferencingItem));
			}
		}

		itemsToUpdate.addAll(items);
	}

	protected boolean relationIsPartOfUniquePropertyConstraint(final E item) {
		return isMappedPropertyUnique;
	}

	protected ModelService getModelService() {
		return Registry.getApplicationContext().getBean(ModelService.class);
	}

	protected QueryService getQueryService() {
		return Registry.getApplicationContext().getBean(QueryService.class);
	}

	public List<? extends Item> getItemsToRemove() {
		return itemsToRemove;
	}

	public List<? extends Item> getItemsToUpdate() {
		return itemsToUpdate;
	}
}

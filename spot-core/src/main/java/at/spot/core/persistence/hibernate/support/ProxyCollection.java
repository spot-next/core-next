package at.spot.core.persistence.hibernate.support;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import org.hibernate.HibernateException;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.CollectionAliases;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.Type;

public abstract class ProxyCollection<E, P extends AbstractPersistentCollection & Collection>
		implements Collection<E>, PersistentCollection {

	private static final long serialVersionUID = 1L;

	protected P proxiedCollection;

	public ProxyCollection(final P proxiedColletion) {
		this.proxiedCollection = proxiedColletion;
	}

	protected void setRelationMapping(final Object value, final Object child) {
		final Object owner = proxiedCollection.getOwner();
		final String propertyName = proxiedCollection.getRole();
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
		return (T[]) proxiedCollection.toArray(a);
	}

	@Override
	public boolean add(final E e) {
		return proxiedCollection.add(e);
	}

	@Override
	public boolean remove(final Object o) {
		return proxiedCollection.remove(o);
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return proxiedCollection.containsAll(c);
	}

	@Override
	public boolean addAll(final Collection<? extends E> c) {
		return proxiedCollection.addAll(c);
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		return proxiedCollection.removeAll(c);
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		return proxiedCollection.retainAll(c);
	}

	@Override
	public void clear() {
		proxiedCollection.clear();
	}

	@Override
	public Object getOwner() {
		return proxiedCollection.getOwner();
	}

	@Override
	public void setOwner(final Object entity) {
		proxiedCollection.setOwner(entity);
	}

	@Override
	public boolean empty() {
		return proxiedCollection.empty();
	}

	@Override
	public void setSnapshot(final Serializable key, final String role, final Serializable snapshot) {
		proxiedCollection.setSnapshot(key, role, snapshot);
	}

	@Override
	public void postAction() {
		proxiedCollection.postAction();
	}

	@Override
	public Object getValue() {
		return proxiedCollection.getValue();
	}

	@Override
	public void beginRead() {
		proxiedCollection.beginRead();
	}

	@Override
	public boolean endRead() {
		return proxiedCollection.endRead();
	}

	@Override
	public boolean afterInitialize() {
		return proxiedCollection.afterInitialize();
	}

	@Override
	public boolean isDirectlyAccessible() {
		return proxiedCollection.isDirectlyAccessible();
	}

	@Override
	public boolean unsetSession(final SharedSessionContractImplementor currentSession) {
		return proxiedCollection.unsetSession(currentSession);
	}

	@Override
	public boolean setCurrentSession(final SharedSessionContractImplementor session) throws HibernateException {
		return proxiedCollection.setCurrentSession(session);
	}

	@Override
	public void initializeFromCache(final CollectionPersister persister, final Serializable disassembled,
			final Object owner) {
		proxiedCollection.initializeFromCache(persister, disassembled, owner);
	}

	@Override
	public Iterator entries(final CollectionPersister persister) {
		return proxiedCollection.entries(persister);
	}

	@Override
	public Object readFrom(final ResultSet rs, final CollectionPersister role, final CollectionAliases descriptor,
			final Object owner) throws HibernateException, SQLException {
		return proxiedCollection.readFrom(rs, role, descriptor, owner);
	}

	@Override
	public Object getIdentifier(final Object entry, final int i) {
		return proxiedCollection.getIdentifier(entry, i);
	}

	@Override
	public Object getIndex(final Object entry, final int i, final CollectionPersister persister) {
		return proxiedCollection.getIndex(entry, i, persister);
	}

	@Override
	public Object getElement(final Object entry) {
		return proxiedCollection.getElement(entry);
	}

	@Override
	public Object getSnapshotElement(final Object entry, final int i) {
		return proxiedCollection.getSnapshotElement(entry, i);
	}

	@Override
	public void beforeInitialize(final CollectionPersister persister, final int anticipatedSize) {
		proxiedCollection.beforeInitialize(persister, anticipatedSize);
	}

	@Override
	public boolean equalsSnapshot(final CollectionPersister persister) {
		return proxiedCollection.equalsSnapshot(persister);
	}

	@Override
	public boolean isSnapshotEmpty(final Serializable snapshot) {
		return proxiedCollection.isSnapshotEmpty(snapshot);
	}

	@Override
	public Serializable disassemble(final CollectionPersister persister) {
		return proxiedCollection.disassemble(persister);
	}

	@Override
	public boolean needsRecreate(final CollectionPersister persister) {
		return proxiedCollection.needsRecreate(persister);
	}

	@Override
	public Serializable getSnapshot(final CollectionPersister persister) {
		return proxiedCollection.getSnapshot(persister);
	}

	@Override
	public void forceInitialization() {
		proxiedCollection.forceInitialization();
	}

	@Override
	public boolean entryExists(final Object entry, final int i) {
		return proxiedCollection.entryExists(entry, i);
	}

	@Override
	public boolean needsInserting(final Object entry, final int i, final Type elemType) {
		return proxiedCollection.needsInserting(entry, i, elemType);
	}

	@Override
	public boolean needsUpdating(final Object entry, final int i, final Type elemType) {
		return proxiedCollection.needsUpdating(entry, i, elemType);
	}

	@Override
	public boolean isRowUpdatePossible() {
		return proxiedCollection.isRowUpdatePossible();
	}

	@Override
	public Iterator getDeletes(final CollectionPersister persister, final boolean indexIsFormula) {
		return proxiedCollection.getDeletes(persister, indexIsFormula);
	}

	@Override
	public boolean isWrapper(final Object collection) {
		return proxiedCollection.isWrapper(collection);
	}

	@Override
	public boolean wasInitialized() {
		return proxiedCollection.wasInitialized();
	}

	@Override
	public boolean hasQueuedOperations() {
		return proxiedCollection.hasQueuedOperations();
	}

	@Override
	public Iterator queuedAdditionIterator() {
		return proxiedCollection.queuedAdditionIterator();
	}

	@Override
	public Collection getQueuedOrphans(final String entityName) {
		return proxiedCollection.getQueuedOrphans(entityName);
	}

	@Override
	public Serializable getKey() {
		return proxiedCollection.getKey();
	}

	@Override
	public String getRole() {
		return proxiedCollection.getRole();
	}

	@Override
	public boolean isUnreferenced() {
		return proxiedCollection.isUnreferenced();
	}

	@Override
	public boolean isDirty() {
		return proxiedCollection.isDirty();
	}

	@Override
	public void clearDirty() {
		proxiedCollection.clear();
	}

	@Override
	public Serializable getStoredSnapshot() {
		return proxiedCollection.getStoredSnapshot();
	}

	@Override
	public void dirty() {
		proxiedCollection.dirty();

	}

	@Override
	public void preInsert(final CollectionPersister persister) {
		proxiedCollection.preInsert(persister);

	}

	@Override
	public void afterRowInsert(final CollectionPersister persister, final Object entry, final int i) {
		proxiedCollection.afterRowInsert(persister, entry, i);

	}

	@Override
	public Collection getOrphans(final Serializable snapshot, final String entityName) {
		return proxiedCollection.getOrphans(snapshot, entityName);
	}

}

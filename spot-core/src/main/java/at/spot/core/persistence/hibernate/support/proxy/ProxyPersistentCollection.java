package at.spot.core.persistence.hibernate.support.proxy;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import org.hibernate.HibernateException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.CollectionAliases;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.Type;

public interface ProxyPersistentCollection extends PersistentCollection {
	@Override
	default Object getOwner() {

		return null;
	}

	PersistentCollection getProxiedColletion();

	@Override
	default void setOwner(final Object entity) {
		getProxiedColletion().setOwner(entity);
	}

	@Override
	default boolean empty() {

		return false;
	}

	@Override
	default void setSnapshot(final Serializable key, final String role, final Serializable snapshot) {

	}

	@Override
	default void postAction() {

	}

	@Override
	default Object getValue() {

		return null;
	}

	@Override
	default void beginRead() {

	}

	@Override
	default boolean endRead() {

		return false;
	}

	@Override
	default boolean afterInitialize() {

		return false;
	}

	@Override
	default boolean isDirectlyAccessible() {

		return false;
	}

	@Override
	default boolean unsetSession(final SharedSessionContractImplementor currentSession) {

		return false;
	}

	@Override
	default boolean setCurrentSession(final SharedSessionContractImplementor session) throws HibernateException {

		return false;
	}

	@Override
	default void initializeFromCache(final CollectionPersister persister, final Serializable disassembled,
			final Object owner) {

	}

	@Override
	default Iterator entries(final CollectionPersister persister) {

		return null;
	}

	@Override
	default Object readFrom(final ResultSet rs, final CollectionPersister role, final CollectionAliases descriptor,
			final Object owner) throws HibernateException, SQLException {

		return null;
	}

	@Override
	default Object getIdentifier(final Object entry, final int i) {

		return null;
	}

	@Override
	default Object getIndex(final Object entry, final int i, final CollectionPersister persister) {

		return null;
	}

	@Override
	default Object getElement(final Object entry) {

		return null;
	}

	@Override
	default Object getSnapshotElement(final Object entry, final int i) {

		return null;
	}

	@Override
	default void beforeInitialize(final CollectionPersister persister, final int anticipatedSize) {

	}

	@Override
	default boolean equalsSnapshot(final CollectionPersister persister) {

		return false;
	}

	@Override
	default boolean isSnapshotEmpty(final Serializable snapshot) {

		return false;
	}

	@Override
	default Serializable disassemble(final CollectionPersister persister) {

		return null;
	}

	@Override
	default boolean needsRecreate(final CollectionPersister persister) {

		return false;
	}

	@Override
	default Serializable getSnapshot(final CollectionPersister persister) {

		return null;
	}

	@Override
	default void forceInitialization() {

	}

	@Override
	default boolean entryExists(final Object entry, final int i) {

		return false;
	}

	@Override
	default boolean needsInserting(final Object entry, final int i, final Type elemType) {

		return false;
	}

	@Override
	default boolean needsUpdating(final Object entry, final int i, final Type elemType) {

		return false;
	}

	@Override
	default boolean isRowUpdatePossible() {

		return false;
	}

	@Override
	default Iterator getDeletes(final CollectionPersister persister, final boolean indexIsFormula) {

		return null;
	}

	@Override
	default boolean isWrapper(final Object collection) {

		return false;
	}

	@Override
	default boolean wasInitialized() {

		return false;
	}

	@Override
	default boolean hasQueuedOperations() {

		return false;
	}

	@Override
	default Iterator queuedAdditionIterator() {

		return null;
	}

	@Override
	default Collection getQueuedOrphans(final String entityName) {

		return null;
	}

	@Override
	default Serializable getKey() {

		return null;
	}

	@Override
	default String getRole() {

		return null;
	}

	@Override
	default boolean isUnreferenced() {

		return false;
	}

	@Override
	default boolean isDirty() {

		return false;
	}

	@Override
	default void clearDirty() {

	}

	@Override
	default Serializable getStoredSnapshot() {

		return null;
	}

	@Override
	default void dirty() {

	}

	@Override
	default void preInsert(final CollectionPersister persister) {

	}

	@Override
	default void afterRowInsert(final CollectionPersister persister, final Object entry, final int i) {

	}

	@Override
	default Collection getOrphans(final Serializable snapshot, final String entityName) {

		return null;
	}
}

package at.spot.core.persistence.hibernate.support;

import java.util.Collection;
import java.util.HashSet;

import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;

public class RelationshipMaintainingSetType extends AbstractCollectionType<PersistentSet> {
	@Override
	public PersistentCollection instantiate(final SharedSessionContractImplementor session,
			final CollectionPersister persister) {

		return super.instantiate(session, PersistentSet.class, null);
	}

	@Override
	public Object instantiate(final int anticipatedSize) {
		return new HashSet<>();
	}

	@Override
	public PersistentCollection wrap(final SharedSessionContractImplementor session, final Object collection) {
		return super.instantiate(session, PersistentSet.class, (Collection) collection);
	}
}
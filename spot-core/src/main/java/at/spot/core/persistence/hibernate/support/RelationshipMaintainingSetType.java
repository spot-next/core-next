package at.spot.core.persistence.hibernate.support;

import java.util.Collection;
import java.util.HashSet;

import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;

public class RelationshipMaintainingSetType extends AbstractCollectionType {
	@Override
	public PersistentCollection instantiate(final SharedSessionContractImplementor session,
			final CollectionPersister persister) {

		return super.instantiateProxy(PersistentSet.class, session);
	}

	@Override
	public Object instantiate(final int anticipatedSize) {
		return super.instantiateProxy(HashSet.class);
	}

	@Override
	public PersistentCollection wrap(final SharedSessionContractImplementor session, final Object collection) {
		return super.instantiateProxy(PersistentSet.class, session, (Collection) collection);
	}
}
package at.spot.core.persistence.hibernate.support;

import java.util.Collection;
import java.util.HashSet;

import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;

public class RelationshipMaintainingCollectionType extends AbstractCollectionType<PersistentBag> {

	@Override
	public PersistentCollection instantiate(final SharedSessionContractImplementor session,
			final CollectionPersister persister) {

		return super.instantiate(session, PersistentBag.class, null);
	}

	@Override
	public Object instantiate(final int anticipatedSize) {
		return new HashSet<>();
	}

	@Override
	public PersistentCollection wrap(final SharedSessionContractImplementor session, final Object collection) {
		return super.instantiate(session, PersistentBag.class, (Collection) collection);
	}

}
package at.spot.core.persistence.hibernate.support;

import java.util.Collection;

import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;

public class RelationshipMaintainingCollectionType extends AbstractCollectionType {

	@Override
	public PersistentCollection instantiate(final SharedSessionContractImplementor session,
			final CollectionPersister persister) {

		return super.instantiateProxy(PersistentBag.class, session);
	}

	@Override
	public Object instantiate(final int anticipatedSize) {
		return super.instantiateProxy(Collection.class);
	}

	@Override
	public PersistentCollection wrap(final SharedSessionContractImplementor session, final Object collection) {
		return super.instantiateProxy(PersistentBag.class, session, (Collection) collection);
	}

}
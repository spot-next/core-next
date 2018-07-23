package at.spot.core.persistence.hibernate.support;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;

public class RelationshipMaintainingListType extends AbstractCollectionType {
	@Override
	public PersistentCollection instantiate(final SharedSessionContractImplementor session,
			final CollectionPersister persister) {

		return super.instantiateProxy(PersistentList.class, session);
	}

	@Override
	public Object instantiate(final int anticipatedSize) {
		return super.instantiateProxy(ArrayList.class);
	}

	@Override
	public PersistentCollection wrap(final SharedSessionContractImplementor session, final Object collection) {
		return super.instantiateProxy(PersistentList.class, session, (Collection) collection);
	}

}
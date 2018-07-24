package at.spot.core.persistence.hibernate.support.usertypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;

import at.spot.core.persistence.hibernate.support.proxy.ProxyPersistentList;

public class RelationshipMaintainingListType extends AbstractCollectionType {
	private static final long serialVersionUID = 1L;

	@Override
	public PersistentCollection instantiate(final SharedSessionContractImplementor session,
			final CollectionPersister persister) {

		return new ProxyPersistentList(new PersistentList(session), null, null);
	}

	@Override
	public Object instantiate(final int anticipatedSize) {
		return new ArrayList<>();
	}

	@Override
	public PersistentCollection wrap(final SharedSessionContractImplementor session, final Object collection) {
		return new ProxyPersistentList(new PersistentList(session, (List) unwrap((Collection<?>) collection)), null,
				null);
	}

}
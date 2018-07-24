package at.spot.core.persistence.hibernate.support.usertypes;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;

import at.spot.core.persistence.hibernate.support.proxy.ProxyPersistentBag;

public class RelationshipMaintainingCollectionType extends AbstractCollectionType {
	private static final long serialVersionUID = 1L;

	@Override
	public PersistentCollection instantiate(final SharedSessionContractImplementor session,
			final CollectionPersister persister) {

		return new ProxyPersistentBag(new PersistentBag(session), null, null);
	}

	@Override
	public Object instantiate(final int anticipatedSize) {
		return new ArrayList<>();
	}

	@Override
	public PersistentCollection wrap(final SharedSessionContractImplementor session, final Object collection) {
		return new ProxyPersistentBag(new PersistentBag(session, unwrap((Collection<?>) collection)), null, null);
	}

}
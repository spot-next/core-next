package at.spot.core.persistence.hibernate.support.usertypes;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.usertype.UserCollectionType;

import at.spot.core.persistence.hibernate.support.proxy.ProxyCollection;

public abstract class AbstractCollectionType implements UserCollectionType, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean contains(final Object collection, final Object entity) {
		return ((Set<?>) collection).contains(entity);
	}

	@Override
	public Object indexOf(final Object collection, final Object entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator getElementsIterator(final Object collection) {
		return ((Set<?>) collection).iterator();
	}

	protected Collection<?> unwrap(final Collection<?> proxyCollection) {
		final Collection<?> collection;

		if (proxyCollection instanceof ProxyCollection) {
			collection = ((ProxyCollection) proxyCollection).getProxiedCollection();
		} else {
			collection = proxyCollection;
		}

		return collection;
	}

	@Override
	public Object replaceElements(final Object original, final Object target, final CollectionPersister persister,
			final Object owner, final Map copyCache, final SharedSessionContractImplementor session)
			throws HibernateException {

		((Set<?>) target).clear();
		((Set<?>) target).addAll((Set) original);
		return target;
	}

}
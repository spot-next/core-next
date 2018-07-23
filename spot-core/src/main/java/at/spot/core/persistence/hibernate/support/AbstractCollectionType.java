package at.spot.core.persistence.hibernate.support;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.usertype.UserCollectionType;

import at.spot.core.persistence.hibernate.support.ProxyCollectionFactory.CollectionInterceptor;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

public abstract class AbstractCollectionType implements UserCollectionType {
	private static final long serialVersionUID = 1L;

	public <T> T instantiateProxy(final Class<T> proxiedType, final Object... constructorArgs) {
		return ProxyCollectionFactory.instantiateProxy(proxiedType, CustomCollectionInterceptor.class, constructorArgs);
	}

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

	@Override
	public Object replaceElements(final Object original, final Object target, final CollectionPersister persister,
			final Object owner, final Map copyCache, final SharedSessionContractImplementor session)
			throws HibernateException {
		((Set<?>) target).clear();
		((Set<?>) target).addAll((Set) original);
		return target;
	}

	public static class CustomCollectionInterceptor extends CollectionInterceptor {
		public static boolean add(@SuperCall final Callable<Boolean> zuper) throws Exception {

			return zuper.call();
		}
	}
}
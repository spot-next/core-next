package at.spot.core.persistence.hibernate.support;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.hibernate.HibernateException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.usertype.UserCollectionType;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;

public abstract class AbstractCollectionType<T extends PersistentCollection> implements UserCollectionType {
	private static final long serialVersionUID = 1L;

	public PersistentCollection instantiate(final SharedSessionContractImplementor session,
			final Class<T> collectionType, final Collection collection) {

		final String proxyClassName = "at.spot.core.persistence.hibernate.support." + collectionType.getSimpleName();
		Class<? extends PersistentCollection> proxyClass = null;
		try {
			proxyClass = (Class<? extends PersistentCollection>) Class.forName(proxyClassName);
		} catch (final ClassNotFoundException e) {
			// ignore
		}
		if (proxyClass == null) {
			try {

				proxyClass = new ByteBuddy().subclass(collectionType).name(proxyClassName) //
						.method(ElementMatchers.named("add"))
						.intercept(MethodDelegation.to(CollectionInterceptor.class)) //
						.make().load(getClass().getClassLoader()).getLoaded();

			} catch (final Exception e) {
				throw new RuntimeException("Could not create proxy collection");
			}
		}

		try {
			if (collection != null) {
				return proxyClass.getConstructor(SharedSessionContractImplementor.class, Set.class).newInstance(session,
						collection);
			} else {
				return proxyClass.getConstructor(SharedSessionContractImplementor.class).newInstance(session);
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException("Could not instantiate proxy collection");
		}
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

	public static class CollectionInterceptor {
		public static boolean add(@SuperCall final Callable<Boolean> zuper) throws Exception {
			return zuper.call();
		}
	}
}
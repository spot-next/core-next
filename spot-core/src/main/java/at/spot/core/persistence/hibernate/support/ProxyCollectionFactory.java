package at.spot.core.persistence.hibernate.support;

import java.util.Optional;
import java.util.concurrent.Callable;

import at.spot.core.support.util.ClassUtil;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;

public class ProxyCollectionFactory {

	public static <T> T instantiateProxy(Class<T> proxiedType,
			Class<? extends CollectionInterceptor> collectionInterceptor, Object... constructorArgs) {

		final String proxyClassName = "at.spot.core.persistence.hibernate.support." + proxiedType.getSimpleName();
		Class<T> proxyClass = null;
		try {
			proxyClass = (Class<T>) Class.forName(proxyClassName);
		} catch (final ClassNotFoundException e) {
			// ignore
		}
		if (proxyClass == null) {
			try {
				proxyClass = (Class<T>) new ByteBuddy().subclass(proxiedType).name(proxyClassName) //
						.method(ElementMatchers.named("add") //
								.or(ElementMatchers.named("addAll") ////
										.or(ElementMatchers.named("remove")//
												.or(ElementMatchers.named("removeAll")
														.or(ElementMatchers.named("removeIf")))))) //
						.intercept(MethodDelegation.to(collectionInterceptor)) //
						.make().load(ClassLoader.getSystemClassLoader()).getLoaded();

			} catch (final Exception e) {
				throw new RuntimeException("Could not create proxy collection");
			}
		}

		final Optional<T> proxyInstance = ClassUtil.instantiate(proxyClass, constructorArgs);

		if (!proxyInstance.isPresent()) {
			throw new RuntimeException("Could not instantiate proxy collection");
		} else {
			return proxyInstance.get();
		}
	}

	public static class CollectionInterceptor {
		public static boolean add(@SuperCall final Callable<Boolean> zuper) throws Exception {
			return zuper.call();
		}

		public static boolean addAll(@SuperCall final Callable<Boolean> zuper) throws Exception {
			return zuper.call();
		}

		public static boolean remove(@SuperCall final Callable<Boolean> zuper) throws Exception {
			return zuper.call();
		}

		public static boolean removeAll(@SuperCall final Callable<Boolean> zuper) throws Exception {
			return zuper.call();
		}

		public static boolean removeIf(@SuperCall final Callable<Boolean> zuper) throws Exception {
			return zuper.call();
		}

		public static boolean clear(@SuperCall final Callable<Boolean> zuper) throws Exception {
			return zuper.call();
		}

	}
}

package io.spotnext.core.infrastructure.support.spring;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ApplicationListenerMethodAdapter;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.EventListenerFactory;
import org.springframework.context.event.EventListenerMethodProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.support.util.ClassUtil;

/**
 * This class alters spring's {@link org.springframework.context.event.EventListenerMethodProcessor} to always register methods annotated with
 * {@link org.springframework.context.event.EventListener} on the root spring context! This enables listeners in child contexts to receive events from the
 * parent contest. Unfortunately the class is not very friendly towards extension (it's an internal class!). So some reflection magic had to take place (see
 * comments in the code).
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class HierarchyAwareEventListenerMethodProcessor extends EventListenerMethodProcessor {

	@Nullable
	private ConfigurableApplicationContext applicationContext;

	@Nullable
	private List<EventListenerFactory> eventListenerFactories;

	@Nullable
	private ConfigurableListableBeanFactory beanFactory;

//	private final EventExpressionEvaluator evaluator = new EventExpressionEvaluator();
	private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		super.postProcessBeanFactory(beanFactory);
		this.beanFactory = beanFactory;

		Map<String, EventListenerFactory> beans = beanFactory.getBeansOfType(EventListenerFactory.class, false, false);
		List<EventListenerFactory> factories = new ArrayList<>(beans.values());
		AnnotationAwareOrderComparator.sort(factories);
		this.eventListenerFactories = factories;
	}

	@SuppressFBWarnings({ "NP_NULL_PARAM_DEREF" })
	@Override
	public void afterSingletonsInstantiated() {
		if (beanFactory == null) {
			throw new BeanInitializationException("Could not register event handlers, beanFactory is null");
		}

		String[] beanNames = beanFactory.getBeanNamesForType(Object.class);

		for (String beanName : beanNames) {
			if (!ScopedProxyUtils.isScopedTarget(beanName)) {
				Class<?> type = null;
				try {
					type = AutoProxyUtils.determineTargetClass(beanFactory, beanName);
				} catch (Throwable ex) {
					// An unresolvable bean type, probably from a lazy bean - let's ignore it.
					if (logger.isDebugEnabled()) {
						logger.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
					}
				}
				if (type != null) {
					if (ScopedObject.class.isAssignableFrom(type)) {
						try {
							Class<?> targetClass = AutoProxyUtils.determineTargetClass(
									beanFactory, ScopedProxyUtils.getTargetBeanName(beanName));
							if (targetClass != null) {
								type = targetClass;
							}
						} catch (Throwable ex) {
							// An invalid scoped proxy arrangement - let's ignore it.
							if (logger.isDebugEnabled()) {
								logger.debug("Could not resolve target bean for scoped proxy '" + beanName + "'", ex);
							}
						}
					}
					try {
						processBean(beanName, type);
					} catch (Throwable ex) {
						throw new BeanInitializationException("Failed to process @EventListener " +
								"annotation on bean with name '" + beanName + "'", ex);
					}
				}
			}
		}
	}

	private void processBean(final String beanName, final Class<?> targetType) {
		if (!this.nonAnnotatedClasses.contains(targetType) && !isSpringContainerClass(targetType)) {
			Map<Method, EventListener> annotatedMethods = null;
			try {
				annotatedMethods = MethodIntrospector.selectMethods(targetType,
						(MethodIntrospector.MetadataLookup<EventListener>) method -> AnnotatedElementUtils.findMergedAnnotation(method, EventListener.class));
			} catch (Throwable ex) {
				// An unresolvable type in a method signature, probably from a lazy bean - let's ignore it.
				if (logger.isDebugEnabled()) {
					logger.debug("Could not resolve methods for bean with name '" + beanName + "'", ex);
				}
			}
			if (CollectionUtils.isEmpty(annotatedMethods)) {
				this.nonAnnotatedClasses.add(targetType);
				if (logger.isTraceEnabled()) {
					logger.trace("No @EventListener annotations found on bean class: " + targetType.getName());
				}
			} else {
				// Non-empty set of methods
				ConfigurableApplicationContext context = this.applicationContext;
				Assert.state(context != null, "No ApplicationContext set");
				List<EventListenerFactory> factories = this.eventListenerFactories;
				Assert.state(factories != null, "EventListenerFactory List not initialized");

				for (Method method : annotatedMethods.keySet()) {
					for (EventListenerFactory factory : factories) {
						if (factory.supportsMethod(method)) {
							Method methodToUse = AopUtils.selectInvocableMethod(method, context.getType(beanName));
							ApplicationListener<?> applicationListener = new ApplicationListenerMethodAdapter(beanName, targetType, methodToUse);

							// CODE CHANGE
							// we only want to register the listener on the root
							// context. We cannot create it as the evaluator is
							// package protected (duh ...)
							// therefore we fetch it using reflection from the
							// super class

							// register the listener in the root context
							ClassUtil.invokeMethod(applicationListener, "init", context, getEvaluator());

							// and add the listener to the root context, instead
							// of the current one!
							getRootContext(context).addApplicationListener(applicationListener);

							// CODE CHANGE

							break;
						}
					}
				}
				if (logger.isDebugEnabled()) {
					logger.debug(annotatedMethods.size() + " @EventListener methods processed on bean '" +
							beanName + "': " + annotatedMethods);
				}
			}
		}
	}

	@SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "false positive")
	protected ConfigurableApplicationContext getRootContext(final ApplicationContext context) {
		ApplicationContext ctx = context;

		while (ctx.getParent() != null) {
			ctx = ctx.getParent();
		}

		return (ConfigurableApplicationContext) ctx;
	}

	protected Set<Class<?>> getNonAnnotatedClasses() {
		return (Set<Class<?>>) ClassUtil.getField(this, "nonAnnotatedClasses", true);
	}

	protected Object getEvaluator() {
		return ClassUtil.getField(this, "evaluator", true);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		super.setApplicationContext(applicationContext);
		this.applicationContext = (ConfigurableApplicationContext) applicationContext;
	}

	/**
	 * Determine whether the given class is an {@code org.springframework} bean class that is not annotated as a user or test {@link Component}... which
	 * indicates that there is no {@link EventListener} to be found there.
	 * 
	 * @since 5.1
	 */
	private static boolean isSpringContainerClass(Class<?> clazz) {
		return (clazz.getName().startsWith("org.springframework.") &&
				!AnnotatedElementUtils.isAnnotated(ClassUtils.getUserClass(clazz), Component.class));
	}
}

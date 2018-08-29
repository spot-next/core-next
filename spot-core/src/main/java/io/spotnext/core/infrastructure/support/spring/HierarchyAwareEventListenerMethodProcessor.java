package io.spotnext.core.infrastructure.support.spring;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ApplicationListenerMethodAdapter;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.EventListenerFactory;
import org.springframework.context.event.EventListenerMethodProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.CollectionUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.support.util.ClassUtil;

/**
 * This class alters spring's {@link org.springframework.context.event.EventListenerMethodProcessor} to always
 * register methods annotated with {@link org.springframework.context.event.EventListener} on the root spring
 * context! This enables listeners in child contexts to receive events from the
 * parent contest. Unfortunately the class is not very friendly towards
 * extension (it's an internal class!). So some reflection magic had to take
 * place (see comments in the code).
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class HierarchyAwareEventListenerMethodProcessor extends EventListenerMethodProcessor {

	private ApplicationContext applicationContext;

	/** {@inheritDoc} */
	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		super.setApplicationContext(applicationContext);
		this.applicationContext = applicationContext;
	}

	@Override
	protected void processBean(final List<EventListenerFactory> factories, final String beanName,
			final Class<?> targetType) {

		if (this.applicationContext != null && !this.getNonAnnotatedClasses().contains(targetType)) {
			Map<Method, EventListener> annotatedMethods = null;
			try {
				annotatedMethods = MethodIntrospector.selectMethods(targetType,
						(MethodIntrospector.MetadataLookup<EventListener>) method -> AnnotatedElementUtils
								.findMergedAnnotation(method, EventListener.class));
			} catch (final Throwable ex) {
				// An unresolvable type in a method signature, probably from a
				// lazy bean - let's ignore it.
				if (logger.isDebugEnabled()) {
					logger.debug("Could not resolve methods for bean with name '" + beanName + "'", ex);
				}
			}
			if (CollectionUtils.isEmpty(annotatedMethods)) {
				getNonAnnotatedClasses().add(targetType);
				if (logger.isTraceEnabled()) {
					logger.trace("No @EventListener annotations found on bean class: " + targetType.getName());
				}
			} else {
				// Non-empty set of methods
				final ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
				for (final Method method : annotatedMethods.keySet()) {
					for (final EventListenerFactory factory : factories) {
						if (factory.supportsMethod(method)) {
							final Method methodToUse = AopUtils.selectInvocableMethod(method,
									context.getType(beanName));
							final ApplicationListener<?> applicationListener = factory
									.createApplicationListener(beanName, targetType, methodToUse);

							// CODE CHANGE
							// we only want to register the listener on the root
							// context. We cannot create it as the evaluator is
							// package protected (duh ...)
							// therefore we fetch it using reflection from the
							// super class
							if (applicationListener instanceof ApplicationListenerMethodAdapter) {
								// register the listener in the root context
								ClassUtil.invokeMethod(applicationListener, "init", context, getEvaluator());
							}

							// and add the listener to the root context, instead
							// of the current one!
							getRootContext(context).addApplicationListener(applicationListener);

							// CODE CHANGE

							break;
						}
					}
				}
				if (logger.isDebugEnabled()) {
					logger.debug(annotatedMethods.size() + " @EventListener methods processed on bean '" + beanName
							+ "': " + annotatedMethods);
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
}

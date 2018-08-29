package io.spotnext.core.infrastructure.aspect;

import java.lang.annotation.Annotation;

import javax.annotation.Resource;

import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import io.spotnext.core.infrastructure.service.LoggingService;
import io.spotnext.core.support.util.ClassUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * <p>Abstract AbstractBaseAspect class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Configurable(autowire = Autowire.BY_TYPE, dependencyCheck = true, preConstruction = true)
@SuppressFBWarnings(value = {
		"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE" }, justification = "AspectJ compile time weaving causes wrong warning")
public abstract class AbstractBaseAspect {

	@Resource
	protected LoggingService loggingService;

	protected <A extends Annotation> A getAnnotation(final JoinPoint joinPoint, final Class<A> annotation) {
		return ClassUtil.getAnnotation(joinPoint, annotation);
	}

	protected String createSignature(final JoinPoint joinPoint) {
		return String.format("%s.%s", joinPoint.getSignature().getClass().getSimpleName(),
				joinPoint.getSignature().getName());
	}

	/**
	 * <p>Getter for the field <code>loggingService</code>.</p>
	 *
	 * @return a {@link io.spotnext.core.infrastructure.service.LoggingService} object.
	 */
	public LoggingService getLoggingService() {
		return loggingService;
	}

	/**
	 * <p>Setter for the field <code>loggingService</code>.</p>
	 *
	 * @param loggingService a {@link io.spotnext.core.infrastructure.service.LoggingService} object.
	 */
	public void setLoggingService(final LoggingService loggingService) {
		this.loggingService = loggingService;
	}

}

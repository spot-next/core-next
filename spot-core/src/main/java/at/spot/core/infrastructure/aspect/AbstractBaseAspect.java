package at.spot.core.infrastructure.aspect;

import java.lang.annotation.Annotation;

import javax.annotation.Resource;

import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.support.util.ClassUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

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

	public LoggingService getLoggingService() {
		return loggingService;
	}

	public void setLoggingService(final LoggingService loggingService) {
		this.loggingService = loggingService;
	}

}

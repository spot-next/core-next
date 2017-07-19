package at.spot.core.infrastructure.aspect;

import java.lang.annotation.Annotation;

import javax.annotation.PostConstruct;

import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.support.util.ClassUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Configurable
@SuppressFBWarnings(value = {
		"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE" }, justification = "AspectJ compile time weaving causes wrong warning")
public abstract class AbstractBaseAspect {

	@Autowired
	protected LoggingService loggingService;

	@PostConstruct
	public void init() {
		loggingService.debug("Initialized abstract base aspect.");
	}

	protected <A extends Annotation> A getAnnotation(final JoinPoint joinPoint, final Class<A> annotation) {
		return ClassUtil.getAnnotation(joinPoint, annotation);
	}

	protected String createSignature(final JoinPoint joinPoint) {
		return String.format("%s.%s", joinPoint.getSignature().getClass().getSimpleName(),
				joinPoint.getSignature().getName());
	}
}

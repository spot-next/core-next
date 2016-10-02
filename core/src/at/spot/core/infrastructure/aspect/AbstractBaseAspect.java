package at.spot.core.infrastructure.aspect;

import java.lang.annotation.Annotation;

import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;

import at.spot.core.infrastructure.service.ClasspathService;

public class AbstractBaseAspect {

	@Autowired
	protected ClasspathService classpathService;

	protected <A extends Annotation> A getAnnotation(JoinPoint joinPoint, Class<A> annotation) {
		return classpathService.getAnnotation(joinPoint, annotation);
	}

	public void setAnnotationService(ClasspathService annotationService) {
		this.classpathService = annotationService;
	}
}

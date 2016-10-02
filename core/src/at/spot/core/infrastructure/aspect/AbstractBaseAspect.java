package at.spot.core.infrastructure.aspect;

import java.lang.annotation.Annotation;

import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;

import at.spot.core.infrastructure.service.TypeService;

public class AbstractBaseAspect {

	@Autowired
	protected TypeService typeService;

	protected <A extends Annotation> A getAnnotation(JoinPoint joinPoint, Class<A> annotation) {
		return typeService.getAnnotation(joinPoint, annotation);
	}

	public void setTypeService(TypeService typeService) {
		this.typeService = typeService;
	}
}

package at.spot.core.infrastructure.service.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.reflections.Reflections;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.annotation.model.Type;
import at.spot.core.infrastructure.service.TypeService;

@Service
public class DefaultTypeService extends AbstractService implements TypeService {

	@Override
	public <A extends Annotation> boolean hasAnnotation(JoinPoint joinPoint, Class<A> annotation) {

		return getAnnotation(joinPoint, annotation) != null;
	}

	public <A extends Annotation> A getAnnotation(JoinPoint joinPoint, Class<A> annotation) {
		A ret = null;

		Signature sig = joinPoint.getSignature();

		if (sig instanceof MethodSignature) {
			final MethodSignature methodSignature = (MethodSignature) sig;
			Method method = methodSignature.getMethod();

			if (method.getDeclaringClass().isInterface()) {
				try {
					method = joinPoint.getTarget().getClass().getMethod(methodSignature.getName());
				} catch (NoSuchMethodException | SecurityException e) {
					//
				}
			}

			ret = AnnotationUtils.findAnnotation(method, annotation);
		} else {
			FieldSignature fieldSignature = (FieldSignature) sig;

			ret = fieldSignature.getField().getAnnotation(annotation);
		}

		return ret;

	}

	@Override
	public List<Class<?>> getItemConcreteTypes(List<String> packages) {
		List<Class<?>> itemTypes = new ArrayList<>();

		for (String pack : packages) {
			Reflections reflections = new Reflections(pack);
			Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Type.class);

			for (Class<?> clazz : annotated) {
				if (clazz.isAnnotationPresent(Type.class)) {
					itemTypes.add(clazz);
				}
			}
		}

		return itemTypes;
	}

	@Override
	public <A extends Annotation> boolean hasAnnotation(Class<? extends Object> type, Class<A> annotation) {
		return getAnnotation(type, annotation) != null;
	}

	@Override
	public <A extends Annotation> A getAnnotation(Class<? extends Object> type, Class<A> annotation) {
		return type.getAnnotation(annotation);
	}
}

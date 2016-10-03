package at.spot.core.infrastructure.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.service.ModelService;

@Aspect
public class ItemPropertyAccessAspect extends AbstractBaseAspect {

	@Autowired
	ModelService modelService;

	@Pointcut("!within(at.spot.core.persistence..*)")
	protected void notFromPersistencePackage() {
	};
	
	/**
	 * Define the pointcut for all fields that are accessed (get) on an object
	 * of type @Item that are annotated with @Property.
	 */
	@Pointcut("@annotation(at.spot.core.infrastructure.annotation.model.Property) && get(* *.*)")
	protected void getAccess() {
	};

	/**
	 * Define the pointcut for all fields that are accessed (set) on an object
	 * of type @Item that are annotated with @Property.
	 */
	@Pointcut("@annotation(at.spot.core.infrastructure.annotation.model.Property) && set(* *.*)")
	protected void setAccess() {
	};

	@Before("getAccess() && notFromPersistencePackage()")
	public void getPropertyValue(JoinPoint joinPoint) {
		Property ann = getAnnotation(joinPoint, Property.class);

		if (ann != null) {
			System.out.println("asdgasawh");
		}
	}

	@Before("setAccess() && notFromPersistencePackage()")
	public void setPropertyValue(JoinPoint joinPoint) {
		Property ann = getAnnotation(joinPoint, Property.class);

	}

}

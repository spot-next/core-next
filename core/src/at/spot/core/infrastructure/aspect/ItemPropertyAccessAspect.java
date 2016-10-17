package at.spot.core.infrastructure.aspect;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import at.spot.core.data.model.Item;
import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.persistence.util.ClassUtil;
import at.spot.core.persistence.valueprovider.ItemPropertyValueProvider;

@Aspect
public class ItemPropertyAccessAspect extends AbstractBaseAspect {

	@Autowired
	ModelService modelService;

	// @Autowired
	Map<String, ItemPropertyValueProvider> itemPropertyValueProviders;

	@Pointcut("!within(at.spot.core.persistence..*) && !within(at.spot.core.infrastructure.aspect..*) && !within(at.spot.core.data.model..*)")
	protected void notFromPersistencePackage() {
	};

	/*
	 * PointCuts
	 */

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

	/*
	 * JoinPoints
	 */

	@After("setAccess() && notFromPersistencePackage()")
	public void setPropertyValue(JoinPoint joinPoint) {
		Property ann = getAnnotation(joinPoint, Property.class);

		if (ann != null && !ann.writable()) {
			throw new RuntimeException(String.format("Attribute %s is not writable.", createSignature(joinPoint)));
		}

		// set the changed field to dirty
		if (joinPoint.getTarget() instanceof Item) {
			ClassUtil.invokeMethod(joinPoint.getTarget(), "markAsDirty", joinPoint.getSignature().getName());
		}
	}

	@Around("getAccess() && notFromPersistencePackage()")
	public Object getPropertyValue(ProceedingJoinPoint joinPoint) throws Throwable {
		Property ann = getAnnotation(joinPoint, Property.class);

		if (ann == null || !ann.readable()) {
			throw new RuntimeException(String.format("Attribute %s is not readable.", createSignature(joinPoint)));
		}

		// if the target is a proxy item, we load it first, then we invoke the
		// getter functionality
		if (joinPoint.getTarget() instanceof Item) {
			Item i = (Item) joinPoint.getTarget();

			if (i.isProxy) {
				modelService.loadProxyModel(i);
			}
		}

		// if there's a value provider configured, use it
		if (StringUtils.isNotBlank(ann.itemValueProvider())) {
			ItemPropertyValueProvider pv = itemPropertyValueProviders.get(ann.itemValueProvider().toString());
			return pv.readValue((Item) joinPoint.getTarget(), joinPoint.getSignature().getName());
		} else { // get currently stored object
			Object retVal = getPropertyValueInternal(joinPoint);
			return retVal;
		}
	}

	protected Item loadFullItem(Item proxyItem) throws ModelNotFoundException {
		if (proxyItem.isProxy) {
			proxyItem = modelService.get(proxyItem.getClass(), proxyItem.pk);
		}

		return proxyItem;
	}

	protected Object getPropertyValueInternal(ProceedingJoinPoint joinPoint) throws Throwable {
		Object[] args = joinPoint.getArgs();
		return joinPoint.proceed(args);
	}
}

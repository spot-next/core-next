package at.spot.core.infrastructure.aspect;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.FieldSignature;
import org.springframework.beans.factory.annotation.Autowired;

import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.type.OneToManyRelationProxyList;
import at.spot.core.model.Item;
import at.spot.core.persistence.service.QueryService;
import at.spot.core.persistence.valueprovider.ItemPropertyValueProvider;
import at.spot.core.support.util.ClassUtil;

@Aspect
public class ItemPropertyAccessAspect extends AbstractBaseAspect {

	@Autowired
	protected ModelService modelService;

	@Autowired
	protected QueryService queryService;

	// @Autowired
	Map<String, ItemPropertyValueProvider> itemPropertyValueProviders;

	/*
	 * PointCuts
	 */

	/**
	 * Write methods on Collection Warning: Does not pick out iterator(), even
	 * though an Iterator can remove elements.
	 */
	@Pointcut("call(boolean Collection+.add(Object)) || call(boolean Collection+.addAll(Collection)) || "
			+ "call(void Collection+.clear()) || call(boolean Collection+.remove(Object)) || "
			+ "call(boolean Collection+.removeAll(Collection)) || call(boolean Collection+.retainAll(Collection))")
	protected void collectionModificationCalls() {
	}

	@Pointcut("!within(at.spot.core.persistence..*) && !within(at.spot.core.infrastructure.aspect..*) && !within(at.spot.core.model..*)")
	protected void notFromPersistencePackage() {
	};

	/**
	 * Define the pointcut for all fields that are accessed (get) on an object
	 * of type @Item that are annotated with @Property.
	 */
	@Pointcut("@annotation(at.spot.core.infrastructure.annotation.model.Property) && get(* *.*)")
	final protected void getAccess() {
	};

	/**
	 * Define the pointcut for all fields that are accessed (set) on an object
	 * of type @Item that are annotated with @Property.
	 */
	@Pointcut("@annotation(at.spot.core.infrastructure.annotation.model.Property) && set(* *.*)")
	final protected void setAccess() {
	};

	/*
	 * JoinPoints
	 */

	@After("setAccess() && notFromPersistencePackage()")
	public void setPropertyValue(final JoinPoint joinPoint) {
		final Property ann = getAnnotation(joinPoint, Property.class);

		if (ann != null && !ann.writable()) {
			throw new RuntimeException(String.format("Attribute %s is not writable.", createSignature(joinPoint)));
		}

		// set the changed field to dirty
		if (joinPoint.getTarget() instanceof Item) {
			ClassUtil.invokeMethod(joinPoint.getTarget(), "markAsDirty", joinPoint.getSignature().getName());
		}
	}

	@Around("getAccess() && notFromPersistencePackage()")
	public Object getPropertyValue(final ProceedingJoinPoint joinPoint) throws Throwable {
		final Property ann = getAnnotation(joinPoint, Property.class);

		if (ann == null || !ann.readable()) {
			throw new RuntimeException(String.format("Attribute %s is not readable.", createSignature(joinPoint)));
		}

		// if the target is a proxy item, we load it first, then we invoke the
		// getter functionality
		if (joinPoint.getTarget() instanceof Item) {
			final Item i = (Item) joinPoint.getTarget();

			if (i.isProxy) {
				modelService.loadProxyModel(i);
			}
		}

		// handle relation annotation
		final Relation rel = getAnnotation(joinPoint, Relation.class);

		if (rel != null) {
			final Object propertyValue = getPropertyValueInternal(joinPoint);

			if (propertyValue == null) {

				final ParameterizedType paramType = (ParameterizedType) ((FieldSignature) joinPoint.getSignature())
						.getField().getGenericType();

				final Class collectionType = (Class) paramType.getActualTypeArguments()[0];

				final List referenceList = new OneToManyRelationProxyList(ArrayList.class, (Item) joinPoint.getTarget(),
						joinPoint.getSignature().getName(), collectionType, rel.referenceProperty(),
						rel.relationItemType(), modelService);

				ClassUtil.setField(joinPoint.getTarget(), joinPoint.getSignature().getName(), referenceList);
			}
		}

		// if there's a value provider configured, use it
		if (StringUtils.isNotBlank(ann.itemValueProvider())) {
			final ItemPropertyValueProvider pv = itemPropertyValueProviders.get(ann.itemValueProvider().toString());
			return pv.readValue((Item) joinPoint.getTarget(), joinPoint.getSignature().getName());
		} else { // get currently stored object
			return getPropertyValueInternal(joinPoint);
		}
	}

	protected Object getPropertyValueInternal(final ProceedingJoinPoint joinPoint) throws Throwable {
		final Object[] args = joinPoint.getArgs();
		return joinPoint.proceed(args);
	}
}

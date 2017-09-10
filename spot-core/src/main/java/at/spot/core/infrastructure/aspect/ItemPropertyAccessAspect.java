package at.spot.core.infrastructure.aspect;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.annotation.SetProperty;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.model.Item;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.persistence.service.QueryService;
import at.spot.core.persistence.service.impl.MapDBService;
import at.spot.core.persistence.valueprovider.ItemPropertyValueProvider;
import at.spot.core.support.util.ClassUtil;

@Aspect
public class ItemPropertyAccessAspect extends AbstractBaseAspect {

	@Resource
	protected PersistenceService persistenceService;

	@Resource
	protected ModelService modelService;

	@Resource
	protected QueryService queryService;

	@PostConstruct
	public void init() {
		loggingService.debug("Initialized item property access aspect.");
	}

	// @Autowired
	Map<String, ItemPropertyValueProvider> itemPropertyValueProviders = new HashMap<>();

	/*
	 * PointCuts
	 */

	@Pointcut("!within(at.spot.core.persistence..*) && !within(at.spot.core.infrastructure.aspect..*)")

	protected void notFromPersistencePackage() {
	};

	/**
	 * Define the pointcut for all fields that are accessed (get) on an object of
	 * type @Item that are annotated with @Property.
	 */
	@Pointcut("@annotation(at.spot.core.infrastructure.annotation.Property) && get(* *.*)")

	final protected void getField() {
	};

	/**
	 * Define the pointcut for all getter that are accessing a field in an object of
	 * type @Item that are annotated with @Property.
	 */
	// @Pointcut("@annotation(at.spot.core.infrastructure.annotation.GetProperty)
	// && within(@at.spot.core.infrastructure.annotation.ItemType *)")
	//
	// final protected void getMethod() {
	// };

	/**
	 * Define the pointcut for all fields that are accessed (set) on an object of
	 * type @Item that are annotated with @Property.
	 */
	@Pointcut("@annotation(at.spot.core.infrastructure.annotation.Property) && set(* *.*)")

	final protected void setField() {
	};

	/**
	 * Define the pointcut for all fields that are accessed (set) on an object of
	 * type @Item that are annotated with @Property.
	 */
	// @Pointcut("@annotation(at.spot.core.infrastructure.annotation.SetProperty)
	// && "
	// + "within(@at.spot.core.infrastructure.annotation.ItemType *) &&
	// execution(* set*(..))")
	//
	// final protected void setMethod() {
	// };

	/*
	 * JoinPoints
	 */

	@After("setField() && notFromPersistencePackage()")
	public void setPropertyValue(final JoinPoint joinPoint) {
		if (isCompatibleWithPersistenceService()) {

			final Property ann = getAnnotation(joinPoint, Property.class);
			final SetProperty setAnn = getAnnotation(joinPoint, SetProperty.class);

			if (setAnn == null && (ann == null || !ann.writable())) {
				throw new RuntimeException(String.format("Attribute %s is not writable.", createSignature(joinPoint)));
			}

			// handle relation annotation
			final Relation rel = getAnnotation(joinPoint, Relation.class);

			if (rel != null) {
				loggingService.warn("Handling relations not implemented here.");
				// handleRelationProperty(joinPoint, rel);
			}

			// set the changed field to dirty
			if (joinPoint.getTarget() instanceof Item) {
				ClassUtil.invokeMethod(joinPoint.getTarget(), "markAsDirty", joinPoint.getSignature().getName());
			}
		}
	}

	@Around("getField() && notFromPersistencePackage()")
	public Object getPropertyValue(final ProceedingJoinPoint joinPoint) throws Throwable {
		if (isCompatibleWithPersistenceService()) {
			final Property ann = getAnnotation(joinPoint, Property.class);
			final GetProperty getAnn = getAnnotation(joinPoint, GetProperty.class);

			if (getAnn == null && (ann == null || !ann.readable())) {
				throw new RuntimeException(String.format("Attribute %s is not readable.", createSignature(joinPoint)));
			}

			// if the target is a proxy item, we load it first, then we invoke the
			// getter functionality
			if (joinPoint.getTarget() instanceof Item) {
				final Item i = (Item) joinPoint.getTarget();

				if (i.isPersisted()) {
					modelService.loadProxyModel(i);
				}
			}

			// if there's a value provider configured, use it
			if (ann != null && StringUtils.isNotBlank(ann.itemValueProvider())) {
				final ItemPropertyValueProvider pv = itemPropertyValueProviders.get(ann.itemValueProvider());
				return pv.readValue((Item) joinPoint.getTarget(), joinPoint.getSignature().getName());
			}
		}

		// get currently stored object
		final Object retVal = getPropertyValueInternal(joinPoint);
		return retVal;
	}

	/**
	 * This aspect is only compatible with the {@link MapDBService}.
	 */
	protected boolean isCompatibleWithPersistenceService() {
		return persistenceService instanceof MapDBService;
	}

	protected Object getPropertyValueInternal(final ProceedingJoinPoint joinPoint) throws Throwable {
		return joinPoint.proceed(joinPoint.getArgs());
	}

	public ModelService getModelService() {
		return modelService;
	}

	public void setModelService(final ModelService modelService) {
		this.modelService = modelService;
	}

	public QueryService getQueryService() {
		return queryService;
	}

	public void setQueryService(final QueryService queryService) {
		this.queryService = queryService;
	}

}

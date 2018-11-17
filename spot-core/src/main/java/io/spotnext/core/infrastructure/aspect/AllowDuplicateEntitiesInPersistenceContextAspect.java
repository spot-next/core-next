package io.spotnext.core.infrastructure.aspect;

import org.apache.commons.lang3.time.FastDateFormat;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.NonUniqueObjectException;

//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.support.Logger;

/**
 * Catches Hibernate's {@link NonUniqueObjectException} in case there are multiple entities with the same PK in the persistence context.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Aspect
//@SuppressFBWarnings("MS_SHOULD_BE_FINAL")
public class AllowDuplicateEntitiesInPersistenceContextAspect {

	static final FastDateFormat DATEFORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

	@Pointcut("execution(* org.hibernate.engine.internal.StatefulPersistenceContext.checkUniqueness(..))")
	final protected void checkUniquenessMethodCalled() {
	};

	/**
	 * @param joinPoint a {@link ProceedingJoinPoint} object.
	 * @return the return value of the intercepted method
	 * @throws Throwable in case the called method throws an uncaught exception
	 */
	@Around("checkUniquenessMethodCalled()")
	public Object checkUniqueness(final ProceedingJoinPoint joinPoint) throws Throwable {

		try {
			return joinPoint.proceed();
		} catch (NonUniqueObjectException e) {
			Logger.debug("Found duplicated hibernate in persistence context ... ignoring");
		}

		return null;
	}

}

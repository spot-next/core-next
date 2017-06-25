package at.spot.core.infrastructure.aspect;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import at.spot.core.infrastructure.annotation.logging.Log;

@Aspect
public class LogAspect extends AbstractBaseAspect {

	/**
	 * Define the pointcut for all methods that are annotated with {@link Log}.
	 */
	@Pointcut("@annotation(at.spot.core.infrastructure.annotation.logging.Log) && execution(* *.*(..))")
	final protected void logAnnotation() {
	};

	// @Before("logAnnotation()")
	// public void logBefore(JoinPoint joinPoint) {
	// Log ann = getAnnotation(joinPoint, Log.class);
	//
	// if (ann != null && ann.before()) {
	// loggingService.log(ann.logLevel(), createLogMessage(joinPoint, true),
	// joinPoint.getTarget().getClass());
	// }
	// }
	//
	// @After("logAnnotation()")
	// public void logAfter(JoinPoint joinPoint) throws AspectException {
	// Log ann = getAnnotation(joinPoint, Log.class);
	//
	// if (ann != null && ann.after()) {
	// loggingService.log(ann.logLevel(), createLogMessage(joinPoint, false),
	// joinPoint.getTarget().getClass());
	// }
	// }

	@Around("logAnnotation()")
	public Object logAround(final ProceedingJoinPoint joinPoint) throws Throwable {
		final Log ann = getAnnotation(joinPoint, Log.class);

		final long startTime = System.currentTimeMillis();

		if (ann != null && ann.before()) {
			loggingService.log(ann.logLevel(),
					createLogMessage(joinPoint, "Before", ann.message(), ann.messageArguments(), null), null, null,
					joinPoint.getTarget().getClass());
		}

		final Object ret = joinPoint.proceed(joinPoint.getArgs());

		if (ann != null && ann.after()) {
			final Long runDuration = ann.measureTime() ? (System.currentTimeMillis() - startTime) : null;

			loggingService.log(ann.logLevel(),
					createLogMessage(joinPoint, "After", null, ann.messageArguments(), runDuration), null, null,
					joinPoint.getTarget().getClass());
		}

		return ret;
	}

	protected String createLogMessage(final JoinPoint joinPoint, final String marker, final String message,
			final String[] arguments, final Long duration) {

		String msg = String.format("%s %s.%s", marker, joinPoint.getTarget().getClass().getSimpleName(),
				joinPoint.getSignature().getName());

		if (StringUtils.isNotBlank(message)) {
			msg = String.format(message, arguments);
		}

		if (duration != null) {
			msg += String.format(" (%s ms)", duration);
		}

		return msg;
	}
}

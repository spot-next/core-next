package io.spotnext.core.infrastructure.aspect;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.TargetClassAware;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.annotation.logging.Log;

/**
 * Annotation-based aspect that logs method execution.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Aspect
@SuppressFBWarnings("MS_SHOULD_BE_FINAL")
public class LogAspect extends AbstractBaseAspect {

	static final FastDateFormat DATEFORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

	/**
	 * The spring init method
	 */
	@PostConstruct
	public void init() {
		getLoggingService().debug("Initialized logging aspect.");
	}

	/**
	 * Define the pointcut for all methods that are annotated with {@link Log}.
	 */
	@Pointcut("@annotation(io.spotnext.core.infrastructure.annotation.logging.Log) && execution(* *.*(..))")
	final protected void logAnnotation() {
	};

	/**
	 * @param joinPoint a {@link ProceedingJoinPoint} object.
	 * @return the return value of the intercepted method
	 * @throws java.lang.Throwable in case there is any error
	 */
	@Around("logAnnotation()")
	public Object logAround(final ProceedingJoinPoint joinPoint) throws Throwable {
		final Log ann = getAnnotation(joinPoint, Log.class);

		final long startTime = System.currentTimeMillis();

		Class<?> targetClass = getRealClass(joinPoint.getTarget());

		if (ann != null && ann.before()) {
			getLoggingService().log(ann.logLevel(),
					createLogMessage(joinPoint, "Before", ann.message(), targetClass, ann.messageArguments(), null), null, null,
					targetClass);
		}

		final Object ret = joinPoint.proceed(joinPoint.getArgs());

		if (ann != null && ann.after()) {
			final Long runDuration = ann.measureTime() ? (System.currentTimeMillis() - startTime) : null;

			getLoggingService().log(ann.logLevel(),
					createLogMessage(joinPoint, "After", null, targetClass, ann.messageArguments(), runDuration), null, null,
					targetClass);
		}

		return ret;
	}

	private Class<?> getRealClass(Object object) {
		Class<?> objectClass = null;

		if (object instanceof TargetClassAware) {
			final TargetClassAware targetAware = ((TargetClassAware) object);

			if (targetAware.getTargetClass() != null) {
				objectClass = targetAware.getTargetClass();
			} else {
				objectClass = targetAware.getClass();
			}
		} else if (object != null) {
			if (object.getClass().getName().contains("CGLIB")) {
				objectClass = object.getClass().getSuperclass();
			} else {
				objectClass = object.getClass();
			}
		}
		return objectClass;
	}

	protected String createLogMessage(final JoinPoint joinPoint, final String marker, final String message, Class<?> klass,
			final Object[] arguments, final Long duration) {

		String msg = null;

		final String className;
		final String classSimpleName;

		if (klass != null) {
			className = klass.getName();
			classSimpleName = klass.getSimpleName();
		} else {
			className = "<null>";
			classSimpleName = "<null>";
		}

		if (StringUtils.isNotBlank(message)) {
			msg = String.format(message, arguments);

			msg = msg.replace("$className", className);
			msg = msg.replace("$classSimpleName", classSimpleName);
			msg = msg.replace("$timestamp", DATEFORMAT.format(new Date()));
		} else {
			msg = String.format("%s %s.%s", marker, classSimpleName, joinPoint.getSignature().getName());
		}

		if (duration != null) {
			msg += String.format(" (%s ms)", duration);
		}

		return msg;
	}
}

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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.annotation.logging.Log;

@Aspect
@SuppressFBWarnings("MS_SHOULD_BE_FINAL")
public class LogAspect extends AbstractBaseAspect {

	static final FastDateFormat DATEFORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

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

	@Around("logAnnotation()")
	public Object logAround(final ProceedingJoinPoint joinPoint) throws Throwable {
		final Log ann = getAnnotation(joinPoint, Log.class);

		final long startTime = System.currentTimeMillis();

		if (ann != null && ann.before()) {
			getLoggingService().log(ann.logLevel(),
					createLogMessage(joinPoint, "Before", ann.message(), ann.messageArguments(), null), null, null,
					joinPoint.getTarget().getClass());
		}

		final Object ret = joinPoint.proceed(joinPoint.getArgs());

		if (ann != null && ann.after()) {
			final Long runDuration = ann.measureTime() ? (System.currentTimeMillis() - startTime) : null;

			getLoggingService().log(ann.logLevel(),
					createLogMessage(joinPoint, "After", null, ann.messageArguments(), runDuration), null, null,
					joinPoint.getTarget().getClass());
		}

		return ret;
	}

	protected String createLogMessage(final JoinPoint joinPoint, final String marker, final String message,
			final Object[] arguments, final Long duration) {

		String msg = null;

		if (StringUtils.isNotBlank(message)) {
			msg = String.format(message, arguments);

			msg = msg.replace("$className", joinPoint.getTarget().getClass().getName());
			msg = msg.replace("$classSimpleName", joinPoint.getTarget().getClass().getSimpleName());
			msg = msg.replace("$timestamp", DATEFORMAT.format(new Date()));
		} else {
			msg = String.format("%s %s.%s", marker, joinPoint.getTarget().getClass().getSimpleName(),
					joinPoint.getSignature().getName());
		}

		if (duration != null) {
			msg += String.format(" (%s ms)", duration);
		}

		return msg;
	}
}

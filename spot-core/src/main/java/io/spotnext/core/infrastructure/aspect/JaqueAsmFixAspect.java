package io.spotnext.core.infrastructure.aspect;

import org.apache.commons.lang3.time.FastDateFormat;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Service;

import aj.org.objectweb.asm.Opcodes;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.core.infrastructure.support.spring.PostConstructor;

/**
 * Intercept Jaque lambda translation and inject ASM7 opcode to make Java 11 happy. This is only a temporary fix!
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Aspect
@Service
public class JaqueAsmFixAspect extends AbstractBaseAspect implements PostConstructor {

	static final FastDateFormat DATEFORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

	@Override
	public void setup() {
		Logger.debug("Initialized Jaque ASM fix.");
	}

	private JaqueAsmFixAspect() {
		setup();
	}

	/**
	 * @param joinPoint a {@link ProceedingJoinPoint} object.
	 * @return the return value of the intercepted method
	 * @throws java.lang.Throwable in case there is any error
	 */
	@Around("call(com.trigersoft.jaque.expression.ExpressionClassVisitor.new(..))")
	public Object interceptConstructorCall(final ProceedingJoinPoint joinPoint) throws Throwable {
		Object[] args = joinPoint.getArgs();

		if (args != null && (args.length == 1 || args.length == 2)) {
			if (args[0] instanceof Integer && ((int) args[0]) != Opcodes.ASM7) {
				args[0] = Opcodes.ASM7;
			}
		}

		return joinPoint.proceed(args);
	}

}

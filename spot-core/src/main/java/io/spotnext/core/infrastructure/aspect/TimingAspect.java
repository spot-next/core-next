package io.spotnext.core.infrastructure.aspect;

import org.aspectj.lang.ProceedingJoinPoint;

//@Aspect
public class TimingAspect {
//  @Around("execution(* *(..)) && cflow(execution(* io.spotnext.core.management.service.impl.ModelServiceRestEndpoint.*(..)))")
  public Object measureExecutionTime(ProceedingJoinPoint thisJoinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();
    Object result = thisJoinPoint.proceed();
    System.out.println(thisJoinPoint + " -> " + (System.currentTimeMillis() - startTime) + " ms");
    return result;
  }
}
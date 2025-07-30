package com.fleencorp.feen.common.aspect.impl;

import com.fleencorp.feen.common.aspect.MeasureExecutionTime;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Aspect for measuring the execution time of methods annotated with {@link MeasureExecutionTime}.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Component
@Aspect
@Slf4j
@NoArgsConstructor
public class ExecutionTimeAspect {

  /**
   * Advice that measures the execution time of methods annotated with {@link MeasureExecutionTime}.
   *
   * @param joinPoint            The join point representing the method being intercepted.
   * @param measureExecutionTime The annotation instance used to mark methods for execution time measurement.
   * @return                     The result of the method invocation.
   * @throws Throwable           If an error occurs during method invocation.
   */
  @Around("@annotation(measureExecutionTime)")
  public Object logExecutionTime(final ProceedingJoinPoint joinPoint, final MeasureExecutionTime measureExecutionTime) throws Throwable {
    // Record the start time of method execution
    final long startTime = System.currentTimeMillis();
    // Proceed with the method execution
    final Object proceed = joinPoint.proceed();
    // Record the end time of method execution
    final long endTime = System.currentTimeMillis();
    // Calculate and log the execution time
    log.info("{} executed in {} ms", joinPoint.getSignature().toShortString(), (endTime - startTime));

    return proceed;
  }
}

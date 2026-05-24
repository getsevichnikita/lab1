package com.library.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.library.service.*.*(..))")
    public Object logExecutionTime(
            ProceedingJoinPoint joinPoint
    ) throws Throwable {

        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long executionTime =
                System.currentTimeMillis() - start;

        log.info(
                "{} executed in {} ms",
                joinPoint.getSignature(),
                executionTime
        );

        return result;
    }
}
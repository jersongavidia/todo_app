package com.backend.puntoxpress.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Log before controller methods
    @Before("execution(* com.backend.puntoxpress.controller.*.*(..))")
    public void logBeforeControllerMethod(JoinPoint joinPoint) {
        logger.info("Executing method: {}", joinPoint.getSignature());
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            logger.debug("Method arguments: {}", (Object) args);
        }
    }

    // Log successful return values
    @AfterReturning(pointcut = "execution(* com.backend.puntoxpress.controller.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Method {} executed successfully", joinPoint.getSignature());
        if (result != null) {
            logger.debug("Method return value: {}", result);
        }
    }

    // Log exceptions
    @AfterThrowing(pointcut = "execution(* com.backend.puntoxpress.controller.*.*(..))", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        logger.error("Exception in method {}: {}", joinPoint.getSignature(), exception.getMessage());
    }
}

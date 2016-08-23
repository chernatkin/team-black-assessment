package com.epages.microservice.handson.shared.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class ValidateAspect {

    private Logger log = LoggerFactory.getLogger(ValidateAspect.class);

    private final Validator validator;

    @Autowired
    public ValidateAspect(Validator validator) {
        log.info("ValidateAspect initialization...");
        this.validator = validator;
    }

    @Pointcut("execution(@com.epages.microservice.handson.shared.validation.Valid * *(..))")
    public void methodWithValidAnnotation() {}

    @Around("methodWithValidAnnotation()")
    public Object validate(ProceedingJoinPoint jp) throws Throwable {

        // Get the target method
        Method interfaceMethod = ((MethodSignature) jp.getSignature()).getMethod();
        Method implementationMethod = jp.getTarget().getClass().getMethod(interfaceMethod.getName(),
                interfaceMethod.getParameterTypes());

        log.info("Validating call to " + implementationMethod.toString());

        // Get the annotated parameters and validate those with the @Valid
        // annotation
        Annotation[][] annotationParameters = implementationMethod.getParameterAnnotations();

        // ConstraintViolations to return
        Set<ConstraintViolation<?>> violations = new HashSet<ConstraintViolation<?>>();

        for (int i = 0; i < annotationParameters.length; i++) {
            Annotation[] annotations = annotationParameters[i];
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(Valid.class)) {
                    Object arg = jp.getArgs()[i];
                    violations.addAll(validator.validate(arg));
                }
            }
        }

        // Throw an exception if ConstraintViolations are found
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        // execute method
        Object returnValue = jp.proceed();

        // can only validate objects, not arrays or primitives..
        if(! interfaceMethod.getReturnType().isPrimitive() && !interfaceMethod.getReturnType().isArray()) {
            // validate response
            violations.addAll(validator.validate(returnValue));

            // Throw an exception if ConstraintViolations are found
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        }

        return returnValue;
    }

}
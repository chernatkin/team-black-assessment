package com.epages.microservice.handson.shared.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class DefaultErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultErrorHandler.class);

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity<DefaultError> handleThrowable(HttpServletRequest request, Throwable throwable) {
        LOGGER.error("Request failed on " + request.getRequestURI(), throwable);
        return new ResponseEntity<>(new DefaultError(throwable), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<DefaultError> handleObjectNotAvailable(HttpServletRequest request, HttpMessageNotReadableException throwable) {
        LOGGER.error("Request failed on " + request.getRequestURI(), throwable);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (throwable.getCause() != null && throwable.getCause().getCause() != null
                && throwable.getCause().getCause() instanceof ObjectRetrievalFailureException) {
            status = HttpStatus.PRECONDITION_FAILED;
        }
        return new ResponseEntity<>(new DefaultError(throwable), status);
    }

    public static class DefaultError {

        private String errorClass;
        private String message;

        public DefaultError(Throwable throwable) {
            setErrorClass(throwable.getClass().getName());
            setMessage(throwable.getMessage());
        }

        public String getErrorClass() {
            return errorClass;
        }

        public void setErrorClass(String errorClass) {
            this.errorClass = errorClass;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}

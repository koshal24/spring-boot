package com.lms.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Override the built-in handler for MethodArgumentNotValidException to provide
     * a consistent JSON response. Overriding avoids creating an additional
     * @ExceptionHandler that can conflict with the handlers defined in the
     * parent ResponseEntityExceptionHandler.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), "Validation failed: " + error.getDefaultMessage()));
        errors.put("message", "One or more fields failed validation. Please check your input.");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}

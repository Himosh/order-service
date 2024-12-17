package com.mini_project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public abstract class AbstractBaseController {

    /**
     * A reusable method to handle exceptions and responses for endpoints.
     *
     * @param supplier  A functional interface supplying the result.
     * @param status    HTTP status to return on success.
     * @param operationDescription Custom message describing the operation being executed.
     * @param <T>       Generic type of the response body.
     * @return          ResponseEntity with the response body and status code.
     */
    protected <T> ResponseEntity<T> handleCustomResponse(Supplier<T> supplier, HttpStatus status, String operationDescription) {
        try {
            log.info("Operation started: {}", operationDescription);
            T result = supplier.get();
            log.info("Operation completed successfully: {}", operationDescription);
            return new ResponseEntity<>(result, status);
        } catch (RuntimeException ex) {
            log.warn("Runtime exception in {}: {}", operationDescription, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception ex) {
            log.error("Error during operation '{}': {}", operationDescription, ex.getMessage(), ex);
            throw new RuntimeException("Error during operation: " + operationDescription, ex);
        }
    }
}

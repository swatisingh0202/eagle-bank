package com.eaglebank.feature.common.exception;

import com.eaglebank.feature.common.web.model.BadRequestErrorResponse;
import com.eaglebank.feature.common.web.model.BadRequestErrorResponseDetailsInner;
import com.eaglebank.feature.common.web.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Slf4j
class EagleBankExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(IncorrectResultSizeDataAccessException ex, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder().message("Resource not found").build();
        return ResponseEntity.status(NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        BadRequestErrorResponse badRequestErrorResponse = BadRequestErrorResponse.builder()
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(BAD_REQUEST).body(badRequestErrorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder().message("Operation is not allowed for the user").build();
        return ResponseEntity.status(FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<?> handleInsufficientFundsException(InsufficientFundsException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder().message("Insufficient funds").build();
        return ResponseEntity.status(UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.builder().message("Unexpected error").build();
        return ResponseEntity.status(NOT_FOUND).body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("MethodArgumentNotValidException: {}", ex.getMessage());
        List<BadRequestErrorResponseDetailsInner> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        BadRequestErrorResponseDetailsInner.builder()
                                .field(error.getField())
                                .type(error.getCode())
                                .message(error.getDefaultMessage())
                                .build()
                )
                .toList();
        BadRequestErrorResponse badRequestErrorResponse = BadRequestErrorResponse.builder()
                .message("Validation error")
                .details(errors)
                .build();
        return ResponseEntity.status(BAD_REQUEST).body(badRequestErrorResponse);
    }
}

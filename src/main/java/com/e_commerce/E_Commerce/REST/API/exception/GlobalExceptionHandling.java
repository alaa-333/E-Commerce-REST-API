package com.e_commerce.E_Commerce.REST.API.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandling {

    /*
        ResourceNotFoundException
     */
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFoundException(CustomerNotFoundException ex , HttpServletRequest request)
    {
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body
                (
                         ErrorResponse.of(ex.getErrorCode().getCode(), ex.getMessage(),HttpStatus.NOT_FOUND.value(), request.getRequestURI())
                );
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFoundException ex , HttpServletRequest request)
    {
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body
                (
                        ErrorResponse.of(ex.getErrorCode().getCode(), ex.getMessage(),HttpStatus.NOT_FOUND.value(), request.getRequestURI())
                );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException ex , HttpServletRequest request)
    {
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body
                (
                        ErrorResponse.of(ex.getErrorCode().getCode(), ex.getMessage(),HttpStatus.NOT_FOUND.value(), request.getRequestURI())
                );
    }


    /*
        (DuplicateResourceFoundException
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceFoundEx(DuplicateResourceException ex, HttpServletRequest request)
    {
        return ResponseEntity.status(HttpStatus.CONFLICT).body( ErrorResponse.of
                (
                        ex.getErrorCode().getCode(),
                        ex.getMessage(),
                        HttpStatus.CONFLICT.value(),
                        request.getRequestURI()

                ));

    }

    @ExceptionHandler(CustomerInactiveException.class)
    public ResponseEntity<ErrorResponse> handleCustomerInactiveException(CustomerInactiveException ex, HttpServletRequest request)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.of(
                                ex.getErrorCode().getCode(),
                                ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value(),
                        request.getRequestURI()
                        )
                );
    }


    @ExceptionHandler(ProductOutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleProductOutOfStockException(ProductOutOfStockException ex , HttpServletRequest request)
    {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(
                        ErrorResponse.of(
                                ex.getErrorCode().getCode(),
                                ex.getMessage(),
                                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStockException(InsufficientStockException ex , HttpServletRequest request)
    {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(
                        ErrorResponse.of(ex.getErrorCode().getCode(),
                                ex.getMessage(),
                                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                                request.getRequestURI()

                        )
                );
    }

    @ExceptionHandler(OrderAlreadyProcessedException.class)
    public ResponseEntity<ErrorResponse> handleOrderAlreadyProcessedException(OrderAlreadyProcessedException ex , HttpServletRequest request)
    {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        ErrorResponse.of(
                                ex.getErrorCode().getCode(),
                                ex.getMessage(),
                                HttpStatus.CONFLICT.value(),
                                request.getRequestURI()

                        )
                );
    }

    @ExceptionHandler (PaymentAmountMismatchException.class)
    public ResponseEntity<ErrorResponse> handlePaymentAmountMismatchException(PaymentAmountMismatchException ex, HttpServletRequest request)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.of(
                                ex.getErrorCode().getCode(),
                                ex.getMessage(),
                                HttpStatus.BAD_REQUEST.value(),
                                request.getRequestURI()
                        )
                );
    }

    /*
       MethodArgumentNotValidException @VALID
    */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex , HttpServletRequest request) {

        // Extract field-specific validation errors
        Map<String, String> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value",
                        (msg1, msg2) -> msg1 // keep the first if duplicates occur
                ));

        // Optional: extract global errors (class-level constraints)
        ex.getBindingResult().getGlobalErrors().forEach(err ->
                validationErrors.put(
                        err.getObjectName(),
                        err.getDefaultMessage() != null ? err.getDefaultMessage() : "Invalid request"
                )
        );

        // Build response
        ErrorResponse response = ErrorResponse.of(ErrorCode.VALIDATION_FAILED.getCode(),
                ErrorCode.VALIDATION_FAILED.getMessage(),
                validationErrors,
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}

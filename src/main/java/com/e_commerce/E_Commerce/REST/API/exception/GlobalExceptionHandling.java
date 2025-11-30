package com.e_commerce.E_Commerce.REST.API.exception;

import com.e_commerce.E_Commerce.REST.API.exception.customer.CustomerInactiveException;
import com.e_commerce.E_Commerce.REST.API.exception.customer.CustomerNotFoundException;
import com.e_commerce.E_Commerce.REST.API.exception.order.OrderAlreadyProcessedException;
import com.e_commerce.E_Commerce.REST.API.exception.order.OrderNotFoundException;
import com.e_commerce.E_Commerce.REST.API.exception.payment.PaymentAmountMismatchException;
import com.e_commerce.E_Commerce.REST.API.exception.product.InsufficientStockException;
import com.e_commerce.E_Commerce.REST.API.exception.product.ProductNotFoundException;
import com.e_commerce.E_Commerce.REST.API.exception.product.ProductOutOfStockException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandling {

    /*
        ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex ,
                                                                         HttpServletRequest request)
    {
        return  ResponseEntity.status(ex.getHttpStatus())
                .body(ErrorResponse.of(
                        ex.getErrorCode().getCode(),
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value(),
                        request.getRequestURI()
                        ));
    }


    /*
        (DuplicateResourceFoundException
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceFoundEx(DuplicateResourceException ex, HttpServletRequest request)
    {
        return ResponseEntity.status(ex.getHttpStatus())
                .body( ErrorResponse.of(
                        ex.getErrorCode().getCode(),
                        ex.getMessage(),
                        ex.getHttpStatus().value(),
                        request.getRequestURI()

                ));

    }

    /*
            Business Exceptions
*/
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request)
    {
        return ResponseEntity.status(ex.getHttpStatus())
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




    /*
        Catch-all for unexpected errors Generic Exception with logger
 */
    @ExceptionHandler(Exception.class) //
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred at {}", request.getRequestURI(), ex); // Log stack trace
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.of(
                            ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                            "An unexpected error occurred",
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            request.getRequestURI()
                    ));
        }

}

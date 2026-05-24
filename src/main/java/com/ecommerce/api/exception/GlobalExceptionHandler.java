package com.ecommerce.api.exception;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for all REST controllers.
 * Converts exceptions into standardized ApiError responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        log.warn("validation exception occur on {} : {}", request.getRequestURI(), ex.getMessage());

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage():"invalid value",
                        (msg1, msg2) -> msg1
                ));
        var response = ErrorResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .errorCode(ErrorCode.VALIDATION_FAILED.getCode())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.badRequest().body(response);

    }


    @ExceptionHandler(EcommerceAppException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            EcommerceAppException ex,
            HttpServletRequest request
    ) {

        log.warn("Business Exception on {} : [{}] {}",
                request.getRequestURI(), ex.getErrorCode().getCode(), ex.getMessage());

        var response = ErrorResponse.builder()
                .success(false).
                errorCode(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .detailedMessage(ex.getDetailedMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {

        log.error("Unexpected exception on {}", request.getRequestURI(), ex);

        var response = ErrorResponse.builder().success(false)
                .errorCode(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                .message(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.internalServerError().body(response);
    }


}

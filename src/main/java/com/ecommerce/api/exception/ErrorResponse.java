package com.ecommerce.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private boolean success;
    private String errorCode;
    private String message;
    private String detailedMessage;
    private LocalDateTime timestamp;
    private String path;
    private Map<String, String> fieldErrors;
}

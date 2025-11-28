package com.e_commerce.E_Commerce.REST.API.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private  LocalDateTime timeStamp;
    private  String message;
    private  String errorCode;
    private int status;
    private  String path;
    private Map<String, String> details;

    public static ErrorResponse of(String errorCode, String message, int status, String path) {
        return ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .message(message)
                .errorCode(errorCode)
                .status(status)
                .path(path)
                .build();
    }
    // Case 1: map needed (Validation errors)
    public static ErrorResponse of(String errorCode, String message,  Map<String, String> details,int status, String path) {
        return ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .message(message)
                .errorCode(errorCode)
                .status(status)
                .path(path)
                .details(details)
                .build();
    }





}

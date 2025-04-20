package com.batubook.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus status;
    private final ErrorCode errorCode;

    public CustomException(HttpStatus status, String message, ErrorCode errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public ErrorDetails getErrorDetails(String path) {
        return ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message(getMessage())
                .path(path)
                .errorCode(errorCode.getCode())
                .details(List.of(errorCode.toString()))
                .build();
    }
}
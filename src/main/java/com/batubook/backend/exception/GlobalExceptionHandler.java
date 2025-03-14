package com.batubook.backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception exception, WebRequest webRequest) {
        logger.error("Unhandled exception: {}", exception.getMessage(), exception);

        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message("An unexpected error occurred.")
                .path(webRequest.getDescription(false))
                .errorCode(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                .details(List.of("Please contact support."))
                .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorDetails> handleCustomException(CustomException customException, WebRequest webRequest) {
        logger.error("Custom exception: {}", customException.getMessage(), customException);

        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message(customException.getMessage())
                .path(webRequest.getDescription(false))
                .errorCode(customException.getErrorCode().getCode())
                .details(List.of(customException.getErrorCode().getDescription()))
                .build();

        return new ResponseEntity<>(errorDetails, customException.getStatus());
    }
}
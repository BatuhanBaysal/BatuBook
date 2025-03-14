package com.batubook.backend.exception;

import org.springframework.http.HttpStatus;

public class CustomExceptions {

    public static class BadRequestException extends CustomException {
        public BadRequestException(String message) {
            super(HttpStatus.BAD_REQUEST, message, ErrorCode.BAD_REQUEST);
        }
    }

    public static class UnauthorizedException extends CustomException {
        public UnauthorizedException(String message) {
            super(HttpStatus.UNAUTHORIZED, message, ErrorCode.UNAUTHORIZED);
        }
    }

    public static class ForbiddenException extends CustomException {
        public ForbiddenException(String message) {
            super(HttpStatus.FORBIDDEN, message, ErrorCode.FORBIDDEN);
        }
    }

    public static class NotFoundException extends CustomException {
        public NotFoundException(String message) {
            super(HttpStatus.NOT_FOUND, message, ErrorCode.NOT_FOUND);
        }
    }

    public static class InternalServerErrorException extends CustomException {
        public InternalServerErrorException(String message) {
            super(HttpStatus.INTERNAL_SERVER_ERROR, message, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
package com.batubook.backend.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    BAD_REQUEST("BAD_REQUEST", "The request is invalid."),
    UNAUTHORIZED("UNAUTHORIZED", "You are not authorized to perform this action."),
    FORBIDDEN("FORBIDDEN", "Access to this resource is forbidden."),
    NOT_FOUND("NOT_FOUND", "The requested resource was not found."),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "An internal server error occurred.");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
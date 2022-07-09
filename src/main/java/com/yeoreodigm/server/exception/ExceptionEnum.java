package com.yeoreodigm.server.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum ExceptionEnum {
    FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN, "403"),
    NO_SUCH_ELEMENT_EXCEPTION(HttpStatus.NOT_FOUND, "404"),
    RUNTIME_EXCEPTION(HttpStatus.CONFLICT, "409");

    private final HttpStatus status;
    private final String code;
    private String message;

    ExceptionEnum(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
    }

    ExceptionEnum(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}

package com.yeoreodigm.server.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private final String message;

    public BadRequestException(String message) {
        this.message = message;
    }

}

package com.yeoreodigm.server.exception;

import lombok.Getter;

@Getter
public class LoginRequiredException extends RuntimeException {

    private final String message;

    public LoginRequiredException(String message) {
        this.message = message;
    }

}

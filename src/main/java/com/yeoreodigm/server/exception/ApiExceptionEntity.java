package com.yeoreodigm.server.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ApiExceptionEntity {

    private final int status;
    private final String error;

    @Builder
    public ApiExceptionEntity(int status, String error) {
        this.status = status;
        this.error = error;
    }
}

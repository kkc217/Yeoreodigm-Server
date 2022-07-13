package com.yeoreodigm.server.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ApiExceptionEntity {

    private final int status;
    private final String errorMessage;

    @Builder
    public ApiExceptionEntity(int status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }
}

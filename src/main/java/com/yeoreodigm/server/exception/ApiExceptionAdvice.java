package com.yeoreodigm.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ApiExceptionAdvice {

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request, final BadRequestException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiExceptionEntity.builder()
                        .status(400)
                        .error(e.getMessage())
                        .build());
    }

}

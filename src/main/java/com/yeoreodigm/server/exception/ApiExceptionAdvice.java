package com.yeoreodigm.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<ApiExceptionEntity> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiExceptionEntity.builder()
                        .status(400)
                        .error("업로드 파일의 용량 너무 큽니다.")
                        .build());
    }
    @ExceptionHandler(LoginRequiredException.class)
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request, final LoginRequiredException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiExceptionEntity.builder()
                        .status(403)
                        .error(e.getMessage())
                        .build());
    }

}

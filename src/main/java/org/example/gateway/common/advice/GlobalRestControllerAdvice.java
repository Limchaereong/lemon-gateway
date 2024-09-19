package org.example.gateway.common.advice;

import org.example.gateway.common.exception.UnAuthorizedException;
import org.example.gateway.common.exception.UnTokenException;
import org.example.gateway.common.exception.payload.ErrorStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalRestControllerAdvice {

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<ErrorStatus> handleApplicationException(UnAuthorizedException e) {
        ErrorStatus errorStatus = e.getErrorStatus();
        return new ResponseEntity<>(errorStatus, errorStatus.toHttpStatus());
    }

    @ExceptionHandler(UnTokenException.class)
    public ResponseEntity<ErrorStatus> handleApplicationException(UnTokenException e) {
        ErrorStatus errorStatus = e.getErrorStatus();
        return new ResponseEntity<>(errorStatus, errorStatus.toHttpStatus());
    }

}
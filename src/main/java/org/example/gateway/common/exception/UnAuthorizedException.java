package org.example.gateway.common.exception;

import lombok.Getter;
import org.example.gateway.common.exception.payload.ErrorStatus;

@Getter
public class UnAuthorizedException extends RuntimeException{

    private final ErrorStatus errorStatus;

    public UnAuthorizedException(ErrorStatus errorStatus) {
        super(errorStatus.message());
        this.errorStatus = errorStatus;
    }

}
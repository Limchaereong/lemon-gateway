package org.example.gateway.common.exception;

import lombok.Getter;
import org.example.gateway.common.exception.payload.ErrorStatus;

@Getter
public class UnTokenException extends RuntimeException{

    private final ErrorStatus errorStatus;

    public UnTokenException(ErrorStatus errorStatus) {
        super(errorStatus.message());
        this.errorStatus = errorStatus;
    }

}
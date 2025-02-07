package org.basoup.gateway.common.exception;

import org.basoup.gateway.common.exception.payload.ErrorCode;

import lombok.Getter;

// 401 - Unauthorized
@Getter
public class UnauthorizedException extends RuntimeException {
	private final ErrorCode errorCode;

	public UnauthorizedException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
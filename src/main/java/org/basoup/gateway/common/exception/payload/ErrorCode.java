package org.basoup.gateway.common.exception.payload;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

	// 400 - Bad Request
	INVALID_REQUEST_ARGUMENT(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	DUPLICATED_VALUE(HttpStatus.BAD_REQUEST, "중복된 값입니다."),

	// 401 - Unauthorized
	UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "Unauthorized access."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),

	// 404 - Not Found
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 리소스를 찾을 수 없습니다."),

	// 500 - Internal Server Error
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부에 문제가 발생했습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	ErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
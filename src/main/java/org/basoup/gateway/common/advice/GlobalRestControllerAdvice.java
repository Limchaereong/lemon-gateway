package org.basoup.gateway.common.advice;

import org.basoup.gateway.common.exception.BadRequestException;
import org.basoup.gateway.common.exception.InternalServerErrorException;
import org.basoup.gateway.common.exception.UnauthorizedException;
import org.basoup.gateway.common.response.ApiResponse;
import org.basoup.gateway.common.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalRestControllerAdvice {

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiResponse<ErrorResponse>> handleBadRequestException(BadRequestException e) {
		ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode());
		return ResponseEntity
			.status(e.getErrorCode().getHttpStatus())
			.body(ApiResponse.error(errorResponse));
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ApiResponse<ErrorResponse>> handleUnauthorizedException(UnauthorizedException e) {
		ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode());
		return ResponseEntity
			.status(e.getErrorCode().getHttpStatus())
			.body(ApiResponse.error(errorResponse));
	}

	@ExceptionHandler(InternalServerErrorException.class)
	public ResponseEntity<ApiResponse<ErrorResponse>> handleInternalServerErrorException(
		InternalServerErrorException e) {
		ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode());
		return ResponseEntity
			.status(e.getErrorCode().getHttpStatus())
			.body(ApiResponse.error(errorResponse));
	}
}
package org.basoup.gateway.common.response;

import java.time.LocalDateTime;

import org.basoup.gateway.common.exception.payload.ErrorCode;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class ErrorResponse {

	private final String message;
	private final int status;
	private final LocalDateTime timestamp;

	public static ErrorResponse of(ErrorCode errorCode) {
		return ErrorResponse.builder()
			.message(errorCode.getMessage())
			.status(errorCode.getHttpStatus().value())
			.timestamp(LocalDateTime.now())
			.build();
	}
}
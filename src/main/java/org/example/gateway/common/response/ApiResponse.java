package org.example.gateway.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {

	private final boolean success;
	private final T data;
	private final ErrorResponse error;

	// 성공 응답
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, data, null);
	}

	// 실패 응답
	public static <T> ApiResponse<T> error(ErrorResponse error) {
		return new ApiResponse<>(false, null, error);
	}
}
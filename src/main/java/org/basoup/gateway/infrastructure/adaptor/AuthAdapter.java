package org.basoup.gateway.infrastructure.adaptor;

import org.basoup.gateway.presentation.dto.request.RefreshTokenRequestDto;
import org.basoup.gateway.presentation.dto.response.TokenResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-server")
public interface AuthAdapter {

	@PostMapping("/auth/refresh")
	TokenResponseDto refreshAccessToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto);

}
package org.example.gateway.common.jwt;

import java.util.Objects;

import org.example.gateway.common.exception.UnauthorizedException;
import org.example.gateway.common.exception.payload.ErrorCode;
import org.example.gateway.infrastructure.adaptor.AuthAdapter;
import org.example.gateway.presentation.dto.request.RefreshTokenRequestDto;
import org.example.gateway.presentation.dto.response.TokenResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

	private final JwtUtil jwtUtil;
	private final AuthAdapter authAdapter;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();

		String path = request.getPath().toString();
		if (path.startsWith("/user/login") || path.startsWith("/auth/refresh")
			|| path.startsWith("/user/signup")) {
			return chain.filter(exchange);
		}

		String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		if (Objects.isNull(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
			log.error("Authorization 헤더가 없거나 유효하지 않습니다.");
			throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_ACCESS);
		}

		String jwtToken = authorizationHeader.substring(7);

		if (!jwtUtil.isTokenValid(jwtToken)) {
			String refreshToken = request.getCookies().getFirst("refresh_token") != null
				? request.getCookies().getFirst("refresh_token").getValue()
				: null;

			if (refreshToken == null) {
				log.error("Refresh token이 없습니다.");
				throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
			}

			RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto(refreshToken);
			TokenResponseDto newTokens = authAdapter.refreshAccessToken(refreshTokenRequestDto);

			log.info("새로운 액세스 토큰: {}", newTokens.accessToken());

			ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + newTokens.accessToken())
				.build();

			return chain.filter(exchange.mutate().request(modifiedRequest).build());
		}

		String userId = jwtUtil.extractUserId(jwtToken);

		log.info("Authenticated user with ID: {} ", userId);

		ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
			.header("X-User-ID", userId)
			.build();

		return chain.filter(exchange.mutate().request(modifiedRequest).build());
	}
}
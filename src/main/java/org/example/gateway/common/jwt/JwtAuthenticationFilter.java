package org.example.gateway.common.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gateway.common.exception.UnAuthorizedException;
import org.example.gateway.common.exception.UnTokenException;
import org.example.gateway.common.exception.payload.ErrorStatus;
import org.example.gateway.infrastructure.adaptor.AuthAdapter;
import org.example.gateway.presentation.dto.request.RefreshTokenRequestDto;
import org.example.gateway.presentation.dto.response.TokenResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final AuthAdapter authAdapter;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (Objects.isNull(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            log.error("Authorization 헤더가 없거나 유효하지 않습니다.");
            throw new UnAuthorizedException(ErrorStatus.toErrorStatus("Authorization 헤더가 없거나 유효하지 않습니다.",
                    HttpStatus.UNAUTHORIZED.value(),
                    LocalDateTime.now() ));
        }

        String jwtToken = authorizationHeader.substring(7);

        if (!jwtUtil.isTokenValid(jwtToken)) {
            String refreshToken = request.getCookies().getFirst("refresh_token") != null
                    ? request.getCookies().getFirst("refresh_token").getValue()
                    : null;

            if (refreshToken == null) {
                log.error("Refresh token이 없습니다.");
                throw new UnTokenException(ErrorStatus.toErrorStatus("리프레시 토큰이 존재하지 않습니다.",
                        HttpStatus.UNAUTHORIZED.value(), LocalDateTime.now()));
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
        String userRole = jwtUtil.extractUserRole(jwtToken);

        log.info("Authenticated user with ID: {} and Role: {}", userId, userRole);

        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("X-User-ID", userId)
                .header("X-User-Role", userRole)
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }
}
package org.example.gateway.common.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gateway.common.exception.UnAuthorizedException;
import org.example.gateway.common.exception.UnTokenException;
import org.example.gateway.common.exception.payload.ErrorStatus;
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
            log.error("유효하지 않은 토큰: {}", jwtToken);
            throw new UnTokenException(ErrorStatus.toErrorStatus("토큰이 유효하지 않습니다.",
                    HttpStatus.UNAUTHORIZED.value(),
                    LocalDateTime.now()));
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
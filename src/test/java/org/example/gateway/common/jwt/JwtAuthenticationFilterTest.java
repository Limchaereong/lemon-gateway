package org.example.gateway.common.jwt;

import org.example.gateway.common.exception.UnAuthorizedException;
import org.example.gateway.common.exception.UnTokenException;
import org.example.gateway.infrastructure.adaptor.AuthAdapter;
import org.example.gateway.presentation.dto.request.RefreshTokenRequestDto;
import org.example.gateway.presentation.dto.response.TokenResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthAdapter authAdapter;

    @Mock
    private WebFilterChain webFilterChain;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(exchange.getRequest()).thenReturn(request);

        RequestPath mockPath = mock(RequestPath.class);
        when(request.getPath()).thenReturn(mockPath);
        when(mockPath.toString()).thenReturn("/test/path");
    }

    @Test
    void testFilter_ValidToken() {
        String validToken = "Bearer validToken";

        ServerHttpRequest.Builder requestBuilder = mock(ServerHttpRequest.Builder.class);
        when(request.mutate()).thenReturn(requestBuilder);

        ServerWebExchange.Builder exchangeBuilder = mock(ServerWebExchange.Builder.class);
        when(exchange.mutate()).thenReturn(exchangeBuilder);

        when(exchangeBuilder.request(any(ServerHttpRequest.class))).thenReturn(exchangeBuilder);
        when(exchangeBuilder.build()).thenReturn(exchange);

        when(request.getHeaders()).thenReturn(new HttpHeaders() {{
            add(HttpHeaders.AUTHORIZATION, validToken);
        }});

        when(jwtUtil.isTokenValid("validToken")).thenReturn(true);
        when(jwtUtil.extractUserId("validToken")).thenReturn("testUserId");
        when(jwtUtil.extractUserRole("validToken")).thenReturn("USER");

        when(requestBuilder.header(anyString(), anyString())).thenReturn(requestBuilder);
        when(requestBuilder.build()).thenReturn(request);

        when(webFilterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        jwtAuthenticationFilter.filter(exchange, webFilterChain).block();

        verify(webFilterChain, times(1)).filter(any(ServerWebExchange.class));
    }

    @Test
    void testFilter_MissingAuthorizationHeader() {
        when(request.getHeaders()).thenReturn(new HttpHeaders());

        assertThrows(UnAuthorizedException.class, () -> {
            jwtAuthenticationFilter.filter(exchange, webFilterChain).block();
        });
    }

    @Test
    void testFilter_InvalidTokenAndValidRefreshToken() {
        String invalidToken = "Bearer invalidToken";

        ServerHttpRequest.Builder requestBuilder = mock(ServerHttpRequest.Builder.class);
        when(request.mutate()).thenReturn(requestBuilder);

        ServerWebExchange.Builder exchangeBuilder = mock(ServerWebExchange.Builder.class);
        when(exchange.mutate()).thenReturn(exchangeBuilder);

        when(exchangeBuilder.request(any(ServerHttpRequest.class))).thenReturn(exchangeBuilder);
        when(exchangeBuilder.build()).thenReturn(exchange);

        when(request.getHeaders()).thenReturn(new HttpHeaders() {{
            add(HttpHeaders.AUTHORIZATION, invalidToken);
        }});

        when(jwtUtil.isTokenValid("invalidToken")).thenReturn(false);

        MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();
        cookies.add("refresh_token", new HttpCookie("refresh_token", "mockRefreshToken"));
        when(request.getCookies()).thenReturn(cookies);

        TokenResponseDto mockResponse = new TokenResponseDto("newAccessToken", "newRefreshToken");
        when(authAdapter.refreshAccessToken(any(RefreshTokenRequestDto.class))).thenReturn(mockResponse);

        when(requestBuilder.header(anyString(), anyString())).thenReturn(requestBuilder);
        when(requestBuilder.build()).thenReturn(request);

        when(webFilterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        jwtAuthenticationFilter.filter(exchange, webFilterChain).block();

        verify(authAdapter, times(1)).refreshAccessToken(any(RefreshTokenRequestDto.class));
        verify(webFilterChain, times(1)).filter(any(ServerWebExchange.class));
    }

    @Test
    void testFilter_InvalidToken_WithoutRefreshToken() {
        String invalidToken = "Bearer invalidToken";
        when(request.getHeaders()).thenReturn(new HttpHeaders() {{
            add(HttpHeaders.AUTHORIZATION, invalidToken);
        }});

        when(jwtUtil.isTokenValid("invalidToken")).thenReturn(false);
        when(request.getCookies()).thenReturn(new LinkedMultiValueMap<>());

        assertThrows(UnTokenException.class, () -> {
            jwtAuthenticationFilter.filter(exchange, webFilterChain).block();
        });
    }
}
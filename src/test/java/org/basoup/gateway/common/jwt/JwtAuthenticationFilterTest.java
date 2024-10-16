package org.basoup.gateway.common.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;

import org.basoup.gateway.common.exception.UnauthorizedException;
import org.basoup.gateway.infrastructure.adaptor.AuthAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

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

		when(requestBuilder.header(anyString(), anyString())).thenReturn(requestBuilder);
		when(requestBuilder.build()).thenReturn(request);

		when(webFilterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

		jwtAuthenticationFilter.filter(exchange, webFilterChain).block();

		verify(webFilterChain, times(1)).filter(any(ServerWebExchange.class));
	}

	@Test
	void testFilter_MissingAuthorizationHeader() {
		when(request.getHeaders()).thenReturn(new HttpHeaders());

		assertThrows(UnauthorizedException.class, () -> {
			jwtAuthenticationFilter.filter(exchange, webFilterChain).block();
		});
	}

	@Test
	void testFilter_InvalidToken_WithoutRefreshToken() {
		String invalidToken = "Bearer invalidToken";
		when(request.getHeaders()).thenReturn(new HttpHeaders() {{
			add(HttpHeaders.AUTHORIZATION, invalidToken);
		}});

		when(jwtUtil.isTokenValid("invalidToken")).thenReturn(false);
		when(request.getCookies()).thenReturn(new LinkedMultiValueMap<>());

		assertThrows(UnauthorizedException.class, () -> {
			jwtAuthenticationFilter.filter(exchange, webFilterChain).block();
		});
	}
}
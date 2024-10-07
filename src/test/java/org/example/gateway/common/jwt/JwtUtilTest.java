package org.example.gateway.common.jwt;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.example.gateway.common.exception.UnauthorizedException;
import org.example.gateway.common.exception.payload.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtUtilTest {

	private JwtUtil jwtUtil;
	private String validToken;
	private final String secretKey = "verysecretkeythatmustbe32characterslong!";
	private final SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

	@BeforeEach
	void setUp() {
		jwtUtil = new JwtUtil(secretKey);

		validToken = Jwts.builder()
			.claim("userId", "testUserId")
			.signWith(key)
			.compact();
	}

	@Test
	void testExtractAllClaims_ValidToken() {
		Claims claims = jwtUtil.extractAllClaims(validToken);
		assertNotNull(claims);
		assertEquals("testUserId", claims.get("userId"));
	}

	@Test
	void testExtractAllClaims_InvalidToken() {
		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
			() -> jwtUtil.extractAllClaims("invalidToken"));
		ErrorCode errorCode = exception.getErrorCode();
		assertEquals(ErrorCode.INVALID_TOKEN, errorCode);
	}

	@Test
	void testExtractUserId() {
		String userId = jwtUtil.extractUserId(validToken);
		assertEquals("testUserId", userId);
	}

	@Test
	void testIsTokenValid_ValidToken() {
		assertTrue(jwtUtil.isTokenValid(validToken));
	}

	@Test
	void testIsTokenValid_InvalidToken() {
		assertFalse(jwtUtil.isTokenValid("invalidToken"));
	}

	@Test
	void testInvalidTokenThrowsException() {
		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
			() -> jwtUtil.extractAllClaims("invalidToken"));
		assertEquals(ErrorCode.INVALID_TOKEN, exception.getErrorCode());
	}
}
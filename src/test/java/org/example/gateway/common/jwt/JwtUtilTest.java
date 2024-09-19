package org.example.gateway.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.gateway.common.exception.UnTokenException;
import org.example.gateway.common.exception.payload.ErrorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String validToken;
    private final String secretKey = "verysecretkeythatmustbe32characterslong!";
    private final SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(secretKey);

        // 유효한 토큰 생성
        validToken = Jwts.builder()
                .claim("userId", "testUserId")
                .claim("userRole", "USER")
                .signWith(key)
                .compact();
    }

    @Test
    void testExtractAllClaims_ValidToken() {
        Claims claims = jwtUtil.extractAllClaims(validToken);
        assertNotNull(claims);
        assertEquals("testUserId", claims.get("userId"));
        assertEquals("USER", claims.get("userRole"));
    }

    @Test
    void testExtractAllClaims_InvalidToken() {
        UnTokenException exception = assertThrows(UnTokenException.class, () -> jwtUtil.extractAllClaims("invalidToken"));
        ErrorStatus errorStatus = exception.getErrorStatus();
        assertEquals("토큰이 유효하지 않습니다.", errorStatus.message());
        assertEquals(401, errorStatus.status());
    }

    @Test
    void testExtractUserId() {
        String userId = jwtUtil.extractUserId(validToken);
        assertEquals("testUserId", userId);
    }

    @Test
    void testExtractUserRole() {
        String userRole = jwtUtil.extractUserRole(validToken);
        assertEquals("USER", userRole);
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
        UnTokenException exception = assertThrows(UnTokenException.class, () -> jwtUtil.extractAllClaims("invalidToken"));
        assertEquals("토큰이 유효하지 않습니다.", exception.getErrorStatus().message());
    }
}
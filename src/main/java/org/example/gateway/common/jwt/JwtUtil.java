package org.example.gateway.common.jwt;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.example.gateway.common.exception.UnauthorizedException;
import org.example.gateway.common.exception.payload.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private final SecretKey secretKey;

	public JwtUtil(@Value("${jwt.secret}") String secretKey) {
		this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
		System.out.println("JWT Secret Key: " + secretKey);
	}

	public Claims extractAllClaims(String token) {
		try {
			return Jwts.parser()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (JwtException e) {
			throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
		}
	}

	public String extractUserId(String token) {
		return extractAllClaims(token).get("userId", String.class);
	}

	public boolean isTokenValid(String token) {
		try {
			Jwts.parser()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (JwtException e) {
			return false;
		}
	}
}
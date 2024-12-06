package com.myinsta.apigateway.service;

import java.security.Key;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.myinsta.apigateway.exception.JwtAuthenticationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author rima.devwork@gmail.com
 *
 */
@Service
@Slf4j
public class JwtService {

	@Value("${jwt.secret}")
	private String secret;

	private Key key;

	@PostConstruct
	public void init() {
		byte[] keyBytes = Base64.getDecoder().decode(secret);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public void validateToken(String token) {
		try {
			Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

			// Validate token type (should be "access")
			String tokenType = claims.getBody().get("token_type", String.class);
			
			if (!"access".equals(tokenType)) {
				throw new JwtException("Invalid token type");
			}
			
		} catch (ExpiredJwtException e) {
			log.error("Token expired: {}", e.getMessage());
			throw new JwtAuthenticationException("Token has expired");
		} catch (SignatureException e) {
			log.error("Invalid JWT signature: {}", e.getMessage());
			throw new JwtAuthenticationException("Invalid token signature");
		} catch (MalformedJwtException e) {
			log.error("Malformed JWT: {}", e.getMessage());
			throw new JwtAuthenticationException("Malformed token");
		} catch (JwtException e) {
			log.error("JWT validation failed: {}", e.getMessage());
			throw new JwtAuthenticationException("JWT validation failed");
		}
	}

	public String extractUserId(String token) {
		try {
			Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
			return claims.getSubject();
		} catch (JwtException e) {
			log.error("Failed to extract user ID: {}", e.getMessage());
			throw new JwtAuthenticationException("Failed to extract user ID from token");
		}
	}

	public List<String> extractRoles(String token) {
		try {
			Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

			@SuppressWarnings("unchecked")
			List<String> roles = claims.get("roles", List.class);
			return roles != null ? roles : Collections.emptyList();
		} catch (JwtException e) {
			log.error("Failed to extract roles: {}", e.getMessage());
			throw new JwtAuthenticationException("Failed to extract roles from token");
		}
	}

	public String extractUsername(String token) {
		try {
			Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
			return claims.get("username", String.class);
		} catch (JwtException e) {
			log.error("Failed to extract username: {}", e.getMessage());
			throw new JwtAuthenticationException("Failed to extract username from token");
		}
	}
}

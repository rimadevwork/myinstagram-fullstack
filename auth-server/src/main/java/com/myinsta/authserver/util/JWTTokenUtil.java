package com.myinsta.authserver.util;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import com.myinsta.authserver.controller.AuthController;
import com.myinsta.authserver.exception.TokenGenerationException;
import com.myinsta.authserver.response.UserValidationResponse;
import com.myinsta.authserver.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTTokenUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(JWTTokenUtil.class);

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Value("${jwt.issuer}")
    private String issuer;
    
    @Value("${jwt.access-token-validity}")
    private Long accessTokenValidity;
    
    @Value("${jwt.refresh-token-validity}")
    private Long refreshTokenValidity;
    

    public String generateAccessToken(UserValidationResponse userDetails) {
        Instant now = Instant.now();
        
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(issuer)
            .subject(userDetails.getUserId())
            .claim("username", userDetails.getUserName())
            .claim("roles", userDetails.getRoles())
            .claim("token_type", "access")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(accessTokenValidity))
            .build();

        try {
            return jwtEncoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
        } catch (JwtException e) {
        	logger.error("Error generating access token: {}", e.getMessage());
            throw new TokenGenerationException("Failed to generate access token", e);
        }
    }

    public String generateRefreshToken(UserValidationResponse userDetails) {
        Instant now = Instant.now();
        
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(issuer)
            .subject(userDetails.getUserId())
            .claim("token_type", "refresh")
            // Minimal claims for refresh token for security
            .issuedAt(now)
            .expiresAt(now.plusSeconds(refreshTokenValidity))
            .build();

        try {
            return jwtEncoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
        } catch (JwtException e) {
        	logger.error("Error generating refresh token: {}", e.getMessage());
            throw new TokenGenerationException("Failed to generate refresh token", e);
        }
    }
    
    public JwtClaimsSet validateRefreshToken(String token) {
        try {
            // Decode the token using JwtDecoder
            Jwt decodedJwt = jwtDecoder.decode(token);
            
            // Validate token type
            String tokenType = decodedJwt.getClaimAsString("token_type");
            if (!"refresh".equals(tokenType)) {
                log.warn("Invalid token type: {}", tokenType);
                return null;
            }
            
            // Validate expiration
            if (decodedJwt.getExpiresAt() != null && 
                decodedJwt.getExpiresAt().isBefore(Instant.now())) {
            	logger.warn("Expired refresh token");
                return null;
            }
            
            return JwtClaimsSet.builder()
                .issuer(decodedJwt.getIssuer().toString())
                .subject(decodedJwt.getSubject())
                .issuedAt(decodedJwt.getIssuedAt())
                .expiresAt(decodedJwt.getExpiresAt())
                .build();
                
        } catch (JwtException e) {
        	logger.error("JWT validation failed: {}", e.getMessage());
            return null;
        }
    }
}
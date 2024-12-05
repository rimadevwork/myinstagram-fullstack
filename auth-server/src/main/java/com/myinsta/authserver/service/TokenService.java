package com.myinsta.authserver.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;

/**
 * Responsible for generating both access tokens and refresh tokens.
 * Since we are not using third-party OAuth providers and are implementing our own Authorization Server, 
 * JWTs are a good choice for access tokens.
 */
@Service
public class TokenService {
	
	@Value("${jwt.secret}")
    private String secretKey;  // Secret key for signing JWT tokens

    @Value("${jwt.expiration-ms}")
    private long expirationTime;  // Token expiration time in milliseconds

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationTime;  // Refresh token expiration time in milliseconds

    // Method to generate access token
    public String generateAccessToken(Map<String, Object> userDetails) {
        return Jwts.builder()
                .setClaims(userDetails)  // Add user details to the claims
                .setSubject((String) userDetails.get("username"))  // Set the subject (user identifier)
                .setIssuedAt(new Date())  // Set issue time
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))  // Set expiration time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // Sign the token using secret key
                .compact();  // Build and return the JWT token
    }

    // Method to generate refresh token
    public String generateRefreshToken(Map<String, Object> userDetails) {
        return Jwts.builder()
                .setClaims(userDetails)  // Add user details to the claims
                .setSubject((String) userDetails.get("username"))  // Set the subject (user identifier)
                .setIssuedAt(new Date())  // Set issue time
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))  // Set expiration time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // Sign the token using secret key
                .compact();  // Build and return the refresh token
    }

    // Method to validate access token (optional, for token validation during protected resource access)
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);  // Parse the JWT using the secret key
            return true;  // Token is valid
        } catch (Exception e) {
            return false;  // Token is invalid or expired
        }
    }

    // Method to get the username from the token (optional)
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();  // Extract the username (subject) from the token
    }

}

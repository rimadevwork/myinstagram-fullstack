package com.myinsta.authserver.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myinsta.authserver.exception.InvalidCredentialsException;
import com.myinsta.authserver.exception.InvalidTokenException;
import com.myinsta.authserver.exception.ServiceUnavailableException;
import com.myinsta.authserver.request.LoginRequest;
import com.myinsta.authserver.response.UserValidationResponse;
import com.myinsta.authserver.service.UserService;
import com.myinsta.authserver.util.JWTTokenUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * @author rime.devwork@gmail.com
 * 
 *This Controller:
 *Handles login requests
 *Validates credentials with User Service
 *Generates JWT tokens for valid users
 *Handles token refresh
 *Includes error handling and logging
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	
    private static final String BEARER_PREFIX = "Bearer ";  // Note the space at the end

    private final UserService userService;
    private final JWTTokenUtil tokenGenerator;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            UserValidationResponse userDetails = userService.validateUser(loginRequest);
            
            // Generate JWT Token using validated user details
            String accessToken = tokenGenerator.generateAccessToken(userDetails);
            String refreshToken = tokenGenerator.generateRefreshToken(userDetails);
           
            return ResponseEntity.ok(Map.of(
                "access_token", accessToken,
                "refresh_token", refreshToken,
                "token_type", "Bearer",
                "expires_in", 3600,
                "user_id", userDetails.getUserId(),
                "username", userDetails.getUserName(),
                "roles", userDetails.getRoles()
            ));
            
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid credentials"));
        } catch (ServiceUnavailableException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
        	logger.error("Login failed for user: " + loginRequest.getUserName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Authentication failed"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (!authHeader.startsWith(BEARER_PREFIX)) {
                throw new InvalidTokenException("Invalid token format");
            }
            
            // Extract token from Bearer header
            String refreshToken = authHeader.substring(BEARER_PREFIX.length());
            
            // Validate refresh token and get user details
            JwtClaimsSet claims = tokenGenerator.validateRefreshToken(refreshToken);
            if (claims == null) {
                throw new InvalidTokenException("Invalid refresh token");
            }
            
            String userId = claims.getSubject();
            
            // Get latest user details using UserService with circuit breaker
            UserValidationResponse userDetails = userService.getUserDetails(userId);
            
            // Generate new access token
            String newAccessToken = tokenGenerator.generateAccessToken(userDetails);
            
            return ResponseEntity.ok(Map.of(
                "access_token", newAccessToken,
                "token_type", BEARER_PREFIX.trim(),
                "expires_in", 3600,
                "user_id", userDetails.getUserId(),
                "username", userDetails.getUserName(),
                "roles", userDetails.getRoles()
            ));
            
        } catch (InvalidTokenException e) {
        	logger.warn("Invalid refresh token attempt: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", e.getMessage()));
                
        } catch (ServiceUnavailableException e) {
        	logger.error("User service unavailable during token refresh", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", e.getMessage()));
                
        } catch (Exception e) {
        	logger.error("Token refresh failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Token refresh failed"));
        }
    }
   
}
package com.myinsta.authserver.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.myinsta.authserver.request.LoginRequest;
import com.myinsta.authserver.response.UserValidationResponse;
import com.myinsta.authserver.exception.ServiceUnavailableException;
import com.myinsta.authserver.exception.InvalidCredentialsException;

@Service
@Slf4j
public class UserService {
    
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final RestTemplate restTemplate;
    
    @Value("${user.service.base-url}")
    private String userServiceBaseUrl;

    @Value("${user.service.validate-url}")
    private String validateUrl;
    
    @Autowired // Add explicit constructor with @Autowired
    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @CircuitBreaker(name = "userService", fallbackMethod = "validateUserFallback")
    public UserValidationResponse validateUser(LoginRequest loginRequest) {
    	logger.info("Attempting to validate user: {}", loginRequest.getUserName());
        
        ResponseEntity<UserValidationResponse> response = restTemplate.postForEntity(
        	validateUrl,
            loginRequest,
            UserValidationResponse.class
        );
        
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
        	logger.error("User validation failed. Status: {}", response.getStatusCode());
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }
    
    private UserValidationResponse validateUserFallback(LoginRequest loginRequest, Exception ex) {
    	logger.error("User service is unavailable. Error: {}", ex.getMessage());
        throw new ServiceUnavailableException("User validation service is temporarily unavailable");
    }
    
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserDetailsFallback")
    public UserValidationResponse getUserDetails(String userId) {
    	logger.info("Fetching user details for ID: {}", userId);
        
        String userDetailsUrl = userServiceBaseUrl + "/api/users/" + userId;

        ResponseEntity<UserValidationResponse> response = restTemplate.getForEntity(userDetailsUrl,
            UserValidationResponse.class
        );
        
        
        
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
        	logger.error("Failed to fetch user details. Status: {}", response.getStatusCode());
            throw new InvalidCredentialsException("User not found");
        }
    }
    
    private UserValidationResponse getUserDetailsFallback(String userId, Exception ex) {
    	logger.error("Failed to fetch user details. Error: {}", ex.getMessage());
        throw new ServiceUnavailableException("User details service is temporarily unavailable");
    }
}
package com.myinsta.apigateway.dto;

import java.time.Instant;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * 
 * @author rima.devwork@gmail.com
 * Optional: DTO for extracted token information
 */
@Data
@Builder
public class TokenInfo {
	private String userId;
	private String username;
	private List<String> roles;
	private Instant expirationTime;
	private String tokenType;
}
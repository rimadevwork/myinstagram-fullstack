package com.myinsta.apigateway.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/**
 * 
 * @author rima.devwork@gmail.com
 * Custom exception for JWT authentication failures
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class JwtAuthenticationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JwtAuthenticationException(String message) {
		super(message);
	}

	public JwtAuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}
}

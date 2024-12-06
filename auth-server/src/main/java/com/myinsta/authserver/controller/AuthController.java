package com.myinsta.authserver.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.myinsta.authserver.request.AuthRequest;
import com.myinsta.authserver.service.ClientService;
import com.myinsta.authserver.service.TokenService;

import jakarta.validation.Valid;

/**
 * @author rima.devwork@gmail.com 
 */

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	// HTTP client to call User Service
	private final RestTemplate restTemplate; 

	private final ClientService clientService;

	private final TokenService tokenService;

	public AuthController(RestTemplate restTemplate, ClientService clientService, TokenService tokenService) {
		this.restTemplate = restTemplate;
		this.clientService = clientService;
		this.tokenService = tokenService;
	}

	@PostMapping("/token")
	public ResponseEntity<?> getToken(@Valid @RequestBody AuthRequest authRequest) {

		if (!clientService.validateClient(authRequest.getClientId(), authRequest.getClientSecret())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid client credentials");
		}

		// Forward credentials to User Service
		ResponseEntity<Map> response = restTemplate.postForEntity("http://user-service/users/validate",
				Map.of("username", authRequest.getUsername(), "password", authRequest.getPassword()), Map.class);

		if (response.getStatusCode() == HttpStatus.OK) {
			// User validated successfully
			Map<String, Object> userDetails = response.getBody();

			// Generate tokens
			String accessToken = tokenService.generateAccessToken(userDetails);
			String refreshToken = tokenService.generateRefreshToken(userDetails);

			// Return tokens
			return ResponseEntity.ok(Map.of("access_token", accessToken, "refresh_token", refreshToken, "token_type",
					"Bearer", "expires_in", 3600));
		} else {
			// Invalid credentials
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
		}

	}

}

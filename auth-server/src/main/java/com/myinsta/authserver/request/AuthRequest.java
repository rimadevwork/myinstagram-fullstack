package com.myinsta.authserver.request;

import jakarta.validation.constraints.NotBlank;

/**
 * @author rima.devwork@gmail.com 
 */

public class AuthRequest {

    @NotBlank(message = "Username is required")
	private String username;
    
    @NotBlank(message = "Password is required")
	private String password;
	
    @NotBlank(message = "Client ID is required")
	private String clientId;
    
    @NotBlank(message = "Client Secret is required")
	private String clientSecret;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

}

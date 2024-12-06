package com.myinsta.authserver.request;

import jakarta.validation.constraints.NotBlank;

/**
 * @author rima.devwork@gmail.com 
 */

public class LoginRequest {

    @NotBlank(message = "userName is required")
	private String userName;
    
    @NotBlank(message = "Password is required")
	private String password;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}

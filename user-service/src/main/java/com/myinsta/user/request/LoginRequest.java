package com.myinsta.user.request;

/**
 * @author rima.devwork@gmail.com 
 */
public class LoginRequest {

	private String userName;
	private String password;
	
	public LoginRequest(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName != null ? userName.toLowerCase() : null;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
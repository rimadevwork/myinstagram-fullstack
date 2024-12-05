package com.myinsta.user.request;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegistrationRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be 8-100 characters")
	private String userName;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8,  max = 100, message = "Password must be 8-100 characters")
	private String password;
    
    @NotBlank(message = "First Name is required")
    @Size(min = 3, max = 100, message = "First Name must be at 3-100 characters")
	private String firstName;

    @NotBlank(message = "Last Name is required")
    @Size(min = 3, max = 100, message = "Last Name must be at 3-100 characters")
    private String lastName;
	
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(min = 8, max = 100, message = "Email must be 8-100 characters")
    private String email;

	private List<String> roles;

	public UserRegistrationRequest() {
	}

	public UserRegistrationRequest(String userName, String password, String firstName, String lastName, String email) {
		this.userName = userName;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email= email;
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

}

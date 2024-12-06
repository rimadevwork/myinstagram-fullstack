package com.myinsta.user.exception;

/**
 * @author rima.devwork@gmail.com 
 */
public class UserAlreadyExistsException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public UserAlreadyExistsException() {
		super();
	}
	
	public UserAlreadyExistsException(String message) {
		super(message);
	}
	
	public UserAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}
}

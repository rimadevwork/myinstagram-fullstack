package com.myinsta.user.exception;

/**
 * @author rima.devwork@gmail.com 
 */
public class InvalidPasswordException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public InvalidPasswordException (String message) {
		super(message);
	}
	
	public InvalidPasswordException(String message, Throwable cause) {
		super(message, cause);
	}
}

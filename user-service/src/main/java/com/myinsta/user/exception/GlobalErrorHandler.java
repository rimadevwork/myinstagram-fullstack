package com.myinsta.user.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.myinsta.user.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 * @author rima.devwork@gmail.com 
 */
@ControllerAdvice
public class GlobalErrorHandler {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	
	
	@ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<String> handleUserAlreadyExistException(UserAlreadyExistsException ex){
		return new ResponseEntity<>("Username is already taken: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
    	logger.debug("User not found: " + ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>("User not found: " + ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<String> handleInvalidPassword(InvalidPasswordException ex) {
    	logger.debug("Invalid password: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>("Invalid password: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleGeneralError(Exception ex) {
    	logger.error("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    	return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

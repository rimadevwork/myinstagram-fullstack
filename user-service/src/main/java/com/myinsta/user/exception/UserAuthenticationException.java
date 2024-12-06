package com.myinsta.user.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author rima.devwork@gmail.com 
 */
public class UserAuthenticationException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public UserAuthenticationException(String msg) {
        super(msg);
    }

    public UserAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

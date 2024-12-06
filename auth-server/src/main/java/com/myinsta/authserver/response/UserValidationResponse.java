package com.myinsta.authserver.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author rima.devwork@gmail.com
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserValidationResponse {
	
    private String userId;
    private List<String> roles;
    private String userName;
}


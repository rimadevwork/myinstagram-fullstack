package com.myinsta.authserver.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author rima.devwork@gmail.com 
 */

@Service
public class ClientService {

	// Since there's only one client, we can hard-code the client credentials in application.properties
	@Value("${registered-client-id}")
    private String REGISTERED_CLIENT_ID;  
	
	@Value("${registered.client-secret}")
    private String REGISTERED_CLIENT_SECRET; 


	public boolean validateClient(String clientId, String clientSecret) {
		return REGISTERED_CLIENT_ID.equals(clientId) && REGISTERED_CLIENT_SECRET.equals(clientSecret);
	}
}

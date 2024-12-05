package com.myinsta.authserver.service;

import org.springframework.stereotype.Service;

@Service
public class ClientService {

	// Since there's only one client, we can hardcode the client credentials

	private static final String REGISTERED_CLIENT_ID = "instagram-client"; // Example client ID
	private static final String REGISTERED_CLIENT_SECRET = "secret"; // Example client secret

	public boolean validateClient(String clientId, String clientSecret) {
		return REGISTERED_CLIENT_ID.equals(clientId) && REGISTERED_CLIENT_SECRET.equals(clientSecret);
	}
}

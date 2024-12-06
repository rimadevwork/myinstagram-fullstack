package com.myinsta.apigateway.validator;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

/**
 * 
 * @author rima.devwork@gmail.com
 * 
 * The class helps determine if a route is public and does not require authentication.
 */
@Component
public class RouteValidator {
	
	/*
	 * Excludes public routes from authentication
	 */
    private final List<String> publicRoutes = Arrays.asList(
        "/api/auth/login",
        "/api/auth/refresh",
        "/api/users/register"
    );

    /*
     * Checks if the current request's URI matches any of the paths in publicRoutes
     */
    public boolean isPublicRoute(ServerHttpRequest request) {
        return publicRoutes.stream()
            .anyMatch(uri -> request.getURI().getPath().contains(uri));
    }
}

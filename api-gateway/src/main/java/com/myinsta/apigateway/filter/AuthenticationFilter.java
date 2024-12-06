package com.myinsta.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import com.myinsta.apigateway.service.JwtService;
import com.myinsta.apigateway.validator.RouteValidator;

/*
 * @author rima.devwork@gmail.com
 * 
 * Filter that checks for and validates JWT token.
 * Validate incoming requests (e.g., check for valid JWT tokens).
 * Determine if the request should be authenticated based on the route.
 * Enforce security or business rules at the gateway level.
 */
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtService jwtService;
    private final RouteValidator routeValidator;

    public AuthenticationFilter(JwtService jwtService, RouteValidator routeValidator) {
        super(Config.class);
        this.jwtService = jwtService;
        this.routeValidator = routeValidator;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            // Skip validation for public endpoints
            if (routeValidator.isPublicRoute(request)) {
                return chain.filter(exchange);
            }

            // Check if Authorization header exists
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new RuntimeException("Missing authorization header");
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new RuntimeException("Invalid authorization header");
            }

            String token = authHeader.substring(7);
            try {
                // Validate token
                jwtService.validateToken(token);
                
                // You can add user details to headers if needed
                ServerHttpRequest modifiedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-User-ID", jwtService.extractUserId(token))
                    .build();
                
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                throw new RuntimeException("Unauthorized access");
            }
        };
    }

    public static class Config {
        // Configuration properties if needed
    	private boolean enabled = true;  // Enable/disable the filter
        private String headerName = "Authorization";  // Custom header name
        
        // Getters and Setters
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public String getHeaderName() {
            return headerName;
        }
        
        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }
    }
}




package com.myinsta.apigateway.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.myinsta.apigateway.filter.AuthenticationFilter;

/**
 * 
 * @author rima.devwork@gmail.com
 * 
 * Defines how incoming HTTP requests should be routed to different microservices.
 * Each service needs to be registered with these exact names (auth-service, user-service) in the Eureka server.
 */
@Configuration
public class GatewayConfig {

    private final AuthenticationFilter authFilter;
    private final ServiceProperties serviceProperties;
    
    @Qualifier("user") 
    private final KeyResolver userKeyResolver;

    public GatewayConfig(AuthenticationFilter authFilter, ServiceProperties serviceProperties, KeyResolver userKeyResolver) {
		super();
		this.authFilter = authFilter;
		this.serviceProperties = serviceProperties;
		this.userKeyResolver = userKeyResolver;
	}

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(serviceProperties.getAuthService().getName(), r -> r
                .path("/api/auth/**")
                .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                .uri(serviceProperties.getAuthService().getUrl()))
            .route(serviceProperties.getUserService().getName(), r -> r
                .path("/api/users/**")
                .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                .uri(serviceProperties.getUserService().getUrl()))
            .build();
    }
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, RedisRateLimiter redisRateLimiter) {
        return builder.routes()
            .route("posts_route", r -> r
                .path("/api/posts/**")
                .filters(f -> f
                    .requestRateLimiter(c -> c
                        .setRateLimiter(redisRateLimiter)
                        .setKeyResolver(userKeyResolver))
                )
                .uri("lb://post-service"))
            .route("users_route", r -> r
                .path("/api/users/**")
                .filters(f -> f
                    .requestRateLimiter(c -> c
                        .setRateLimiter(redisRateLimiter)
                        .setKeyResolver(userKeyResolver))
                )
                .uri("lb://user-service"))
            .route("comments_route", r -> r
                .path("/api/comments/**")
                .filters(f -> f
                    .requestRateLimiter(c -> c
                        .setRateLimiter(redisRateLimiter)
                        .setKeyResolver(userKeyResolver))
                )
                .uri("lb://comment-service"))
            .build();
    }
}
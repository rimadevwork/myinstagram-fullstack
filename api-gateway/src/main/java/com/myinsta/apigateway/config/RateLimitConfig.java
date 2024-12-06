package com.myinsta.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;

/**
 * 
 * @author rima.devwork@gmail.com
 * 
 * Uses Redis as a rate limiter store.
 * Sets a default rate of 5 requests per second with a burst capacity of 10.
 * Applies rate limiting per user (falls back to 'anonymous' for unauthenticated requests)
 * Applies to all major API endpoints (posts, users, comments)
 * The rate limits can be customized for different endpoints by creating multiple RedisRateLimiter beans.
 */
@Configuration
public class RateLimitConfig {
	
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        // replenishRate: how many requests per second you want a user to be allowed to do
        // burstCapacity: maximum number of requests a user can do in a single second
        return new RedisRateLimiter(5, 10);
    }

    /**
     * It tries to get the authenticated user's principal (username) from the request
     * If there's no authenticated user, it falls back to "anonymous"
     * This means all authenticated users get their own rate limit bucket, while all unauthenticated requests share one "anonymous" bucket
     * @return
     */
    @Bean
    @Primary 
    @Qualifier("user")
    KeyResolver userKeyResolver() {
        return exchange -> exchange.getPrincipal()
            .map(principal -> principal.getName())
            .defaultIfEmpty("anonymous");
    }
    
    
    // Rate limit by IP address
    @Bean
    @Qualifier("ip")
    KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
            exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
        );
    }

    // Rate limit by request path
    @Bean
    @Qualifier("path")
    KeyResolver pathKeyResolver() {
        return exchange -> Mono.just(
            exchange.getRequest().getPath().toString()
        );
    }

    // Rate limit by API key in header
    @Bean
    @Qualifier("api")
    KeyResolver apiKeyResolver() {
        return exchange -> Mono.just(
            exchange.getRequest().getHeaders().getFirst("X-API-Key")
        );
    }

    // Combine multiple identifiers
    @Bean
    @Qualifier("combined")
    KeyResolver combinedKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            String path = exchange.getRequest().getPath().toString();
            return Mono.just(ip + ":" + path);
        };
    }

}

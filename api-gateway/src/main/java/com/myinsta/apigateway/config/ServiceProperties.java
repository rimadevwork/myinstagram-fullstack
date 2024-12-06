package com.myinsta.apigateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 
 * @author rima.devwork@gmail.com
 *
 */
@Configuration
@ConfigurationProperties(prefix = "app.services")
@Data
public class ServiceProperties {
	
    private ServiceConfig authService;
    private ServiceConfig userService;
    
    @Data
    public static class ServiceConfig {
        private String name;
        private String url;
    }
}
package com.myinsta.authserver.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author rima.devwork@gmail.com 
 */
@Configuration
public class AuthorizationServerConfig {

	/**
	 * 
	 * @LoadBalanced to enable it to perform client-side load balancing using Eureka.
	 */
	@Bean
    @LoadBalanced 
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

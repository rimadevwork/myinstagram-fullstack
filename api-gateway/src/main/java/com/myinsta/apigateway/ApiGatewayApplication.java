package com.myinsta.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiGatewayApplication {

    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayApplication.class);

	public static void main(String[] args) {
        logger.debug("ApiGatewayApplication starting");
		SpringApplication.run(ApiGatewayApplication.class, args);
        logger.debug("ApiGatewayApplication started succesfully.");
	}

}
